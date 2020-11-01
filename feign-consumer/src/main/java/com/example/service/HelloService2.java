package com.example.service;

import org.springframework.cloud.netflix.feign.FeignClient;

@FeignClient("hello-service")
public interface HelloService2 extends HelloService {
}