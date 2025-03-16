package com.ldw.microservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservice-uc")
public interface UserServiceFeign {
    @GetMapping("getUserById")
    String getUserById();
}