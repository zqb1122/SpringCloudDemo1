package com.example.service;

import com.example.entity.Book;
import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCollapserKey;
import com.netflix.hystrix.HystrixCollapserProperties;
import com.netflix.hystrix.HystrixCommand;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BookCollapseCommand extends HystrixCollapser<List<Book>, Book, Long> {
    private BookService bookService;
    private Long id;

    public BookCollapseCommand(BookService bookService, Long id) {
        super(Setter.withCollapserKey(HystrixCollapserKey.Factory.asKey("bookCollapseCommand")).andCollapserPropertiesDefaults(HystrixCollapserProperties.Setter().withTimerDelayInMilliseconds(100)));
        this.bookService = bookService;
        this.id = id;
    }

    @Override
    public Long getRequestArgument() {
        return id;
    }

    @Override
    protected HystrixCommand<List<Book>> createCommand(Collection<CollapsedRequest<Book, Long>> collapsedRequests) {
        List<Long> ids = new ArrayList<>(collapsedRequests.size());
        ids.addAll(collapsedRequests.stream().map(CollapsedRequest::getArgument).collect(Collectors.toList()));
        BookBatchCommand bookBatchCommand = new BookBatchCommand(ids, bookService);
        return bookBatchCommand;
    }

    @Override
    protected void mapResponseToRequests(List<Book> batchResponse, Collection<CollapsedRequest<Book, Long>> collapsedRequests) {
        System.out.println("mapResponseToRequests");
        int count = 0;
        for (CollapsedRequest<Book, Long> collapsedRequest : collapsedRequests) {
            Book book = batchResponse.get(count++);
            collapsedRequest.setResponse(book);
        }
    }
}