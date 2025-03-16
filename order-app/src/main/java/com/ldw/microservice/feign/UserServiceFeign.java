package com.ldw.microservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "microservice-uc" , url = "http://192.168.172.129:8001")
public interface UserServiceFeign {
    @GetMapping("getUserById")
    String getUserById();
}