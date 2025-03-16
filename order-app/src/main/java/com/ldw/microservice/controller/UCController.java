package com.ldw.microservice.controller;

import com.ldw.common.vo.Result;
import com.ldw.microservice.feign.OrderFeign;
import com.ldw.microservice.feign.UserServiceFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UCController {
    @Autowired
    UserServiceFeign userServiceFeign;

    @Autowired
    OrderFeign orderFeign;

    @GetMapping("/getUserById")
    public Result getUserById(Long deptId) {
        String username = userServiceFeign.getUserById();
        return Result.success(username);
    }


    @GetMapping("/getOrder")
    public Result getOrder(Long deptId) {
        String username = orderFeign.getOrder();
        return Result.success(username);
    }
}
