package com.ldw.microservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "microservice-order")
public interface OrderFeign {
    @GetMapping("getOrder")
    String getOrder();
}