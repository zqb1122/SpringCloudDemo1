package com.example.service;

import com.example.entity.Book;
import com.netflix.hystrix.HystrixCommand;
import org.springframework.web.client.RestTemplate;

public class BookCommand extends HystrixCommand<Book> {

    private RestTemplate restTemplate;
    private long id;

    @Override
    protected Book getFallback() {
        Throwable executionException = getExecutionException();
        System.out.println(executionException.getMessage());
        return new Book("宋诗选注", 88, "钱钟书", "三联书店");
    }

    public BookCommand(Setter setter, RestTemplate restTemplate) {
        super(setter);
        this.restTemplate = restTemplate;
    }

    public BookCommand(Setter setter, RestTemplate restTemplate, long id) {
        super(setter);
        this.restTemplate = restTemplate;
        this.id = id;

    }

    @Override
    protected Book run() throws Exception {
        System.out.println("test5");
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
    }

    @Override
    protected String getCacheKey() {
        return String.valueOf(id);
    }
}