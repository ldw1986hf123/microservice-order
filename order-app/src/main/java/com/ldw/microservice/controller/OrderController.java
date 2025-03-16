package com.ldw.microservice.controller;

import com.ldw.microservice.feign.OrderFeign;
import com.ldw.microservice.feign.UserServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ldw.common.vo.Result;

@RestController
public class OrderController {

    @GetMapping("/getOrder")
    public Result getOrder(Long deptId) {
        String username = "order";
        return Result.success(username);
    }

}
