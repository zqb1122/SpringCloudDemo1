package com.example.service;

import com.example.entity.Book;
import com.netflix.hystrix.HystrixCommand;
import org.springframework.web.client.RestTemplate;

public class BookCommand extends HystrixCommand<Book> {

    private RestTemplate restTemplate;

    @Override
    protected Book getFallback() {
        return new Book("宋诗选注", 88, "钱钟书", "三联书店");
    }

    public BookCommand(Setter setter, RestTemplate restTemplate) {
        super(setter);
        this.restTemplate = restTemplate;
    }

    @Override
    protected Book run() throws Exception {
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook1", Book.class);
    }
}