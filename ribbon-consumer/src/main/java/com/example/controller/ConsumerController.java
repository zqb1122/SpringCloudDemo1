package com.example.controller;

import com.example.entity.Book;
import com.example.service.BookCommand;
import com.example.service.HelloService;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixConcurrencyStrategyDefault;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
public class ConsumerController {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private HelloService helloService;

    @RequestMapping(value = "/ribbon-consumer", method = RequestMethod.GET)
    public String helloController() {
        return helloService.hello();
    }

    @RequestMapping("/gethello")
    public String getHello() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class);
        String body = responseEntity.getBody();
        HttpStatus statusCode = responseEntity.getStatusCode();
        int statusCodeValue = responseEntity.getStatusCodeValue();
        HttpHeaders headers = responseEntity.getHeaders();
        StringBuffer result = new StringBuffer();
        result.append("responseEntity.getBody()：").append(body).append("<hr>")
                .append("responseEntity.getStatusCode()：").append(statusCode).append("<hr>")
                .append("responseEntity.getStatusCodeValue()：").append(statusCodeValue).append("<hr>")
                .append("responseEntity.getHeaders()：").append(headers).append("<hr>");
        return result.toString();
    }

    @RequestMapping("/book1")
    public Book book1() {
        ResponseEntity<Book> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/getbook1", Book.class);
        return responseEntity.getBody();
    }

    @RequestMapping("/book2")
    public Book book2() {
        Book book = restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
        return book;
    }

    @RequestMapping("/book3")
    public Book book3() {
        Book book = new Book();
        book.setName("红楼梦");
        ResponseEntity<Book> responseEntity = restTemplate.postForEntity("http://HELLO-SERVICE/getbook2", book, Book.class);
        return responseEntity.getBody();
    }


    @RequestMapping("/test1")
    public Book test1() throws ExecutionException, InterruptedException {
        BookCommand bookCommand = new BookCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("")), restTemplate);
        //同步调用
        //Book book1 = bookCommand.execute();
        //异步调用
        Future<Book> queue = bookCommand.queue();
        Book book = queue.get();
        return book;
    }

    @RequestMapping("/test2")
    public Book test2() throws ExecutionException, InterruptedException {
        Book book = helloService.test2();
        //调用get方法时也可以设置超时时长
        return book;
    }

    @RequestMapping("/test3")
    public Book test3() throws ExecutionException, InterruptedException {
        Future<Book> bookFuture = helloService.test3();
        //调用get方法时也可以设置超时时长
        return bookFuture.get();
    }


    @RequestMapping(value = "/getbook5/{id}", method = RequestMethod.GET)
    public Book book5(@PathVariable("id") Integer id) {
        System.out.println(">>>>>>>>/getbook5/{id}");
        if (id == 1) {
            return new Book("《李自成》", 55, "姚雪垠", "人民文学出版社");
        } else if (id == 2) {
            return new Book("中国文学简史", 33, "林庚", "清华大学出版社");
        }
        return new Book("文学改良刍议", 33, "胡适", "无");
    }

    @RequestMapping("/test5")
    public Book test5() {
        HystrixCommandKey commandKey = HystrixCommandKey.Factory.asKey("commandKey");
        HystrixRequestContext.initializeContext();
        BookCommand bc1 = new BookCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("")).andCommandKey(commandKey), restTemplate, 1l);
        Book e1 = bc1.execute();
        HystrixRequestCache.getInstance(commandKey, HystrixConcurrencyStrategyDefault.getInstance()).clear(String.valueOf(1l));
        BookCommand bc2 = new BookCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("")).andCommandKey(commandKey), restTemplate, 1l);
        Book e2 = bc2.execute();
        BookCommand bc3 = new BookCommand(HystrixCommand.Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("")).andCommandKey(commandKey), restTemplate, 1l);
        Book e3 = bc3.execute();
        System.out.println("e1:" + e1);
        System.out.println("e2:" + e2);
        System.out.println("e3:" + e3);
        return e1;
    }

    @RequestMapping("/test6")
    public Book test6() {
        HystrixRequestContext.initializeContext();
        //第一次发起请求
        Book b1 = helloService.test6(2, "");
        //参数和上次一致，使用缓存数据
        Book b2 = helloService.test6(2, "");
        //参数不一致，发起新请求
        Book b3 = helloService.test6(2, "aa");
        return b1;
    }
}