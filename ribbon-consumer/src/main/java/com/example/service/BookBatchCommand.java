package com.example.service;

import com.example.entity.Book;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;

import java.util.List;

public class BookBatchCommand extends HystrixCommand<List<Book>> {
    private List<Long> ids;
    private BookService bookService;

    public BookBatchCommand(List<Long> ids, BookService bookService) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("CollapsingGroup"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("CollapsingKey")));
        this.ids = ids;
        this.bookService = bookService;
    }

    @Override
    protected List<Book> run() throws Exception {
        return bookService.test9(ids);
    }
}