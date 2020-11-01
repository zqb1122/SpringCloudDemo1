package com.example.service;


import com.example.entity.Book;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class BookService {
    @Autowired
    private RestTemplate restTemplate;
    public Book test8(Long id) {
        return restTemplate.getForObject("http://HELLO-SERVICE/getbook6/{1}", Book.class, id);
    }

    public List<Book> test9(List<Long> ids) {
        System.out.println("test9---------"+ids+"Thread.currentThread().getName():" + Thread.currentThread().getName());
        Book[] books = restTemplate.getForObject("http://HELLO-SERVICE/getbook6?ids={1}", Book[].class, StringUtils.join(ids, ","));
        return Arrays.asList(books);
    }
}
