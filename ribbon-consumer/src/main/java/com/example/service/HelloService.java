package com.example.service;

import com.example.entity.Book;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.cache.annotation.CacheResult;
import com.netflix.hystrix.contrib.javanica.command.AsyncResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Future;

@Service
public class HelloService {
    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "error")
    public String hello() {
        ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://HELLO-SERVICE/hello", String.class);
        return responseEntity.getBody();
    }

    public String error() {
        return "error";
    }


    @HystrixCommand
    public Future<Book> test3() {
        return new AsyncResult<Book>() {
            @Override
            public Book invoke() {
                return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
            }
        };
    }

    @HystrixCommand(fallbackMethod = "error1", ignoreExceptions = ArithmeticException.class)
    public Book test2() {
        int i = 1 / 0;
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
    }

    public Book error1(Throwable throwable) {
        System.out.println(throwable.getMessage());
        return new Book("百年孤独", 33, "马尔克斯", "人民文学出版社");
    }

    @CacheResult(cacheKeyMethod = "getCacheKey2")
    @HystrixCommand
    public Book test6(Integer id,String aa) {
        System.out.println("test6");
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class, id);
    }

    public String getCacheKey2(Integer id) {
        return String.valueOf(id);
    }
}