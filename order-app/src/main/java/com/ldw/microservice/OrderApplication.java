package com.ldw.microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableDiscoveryClient  // 启用 Nacos 服务发现
@EnableFeignClients // 确保扫描到 Feign 客户端
public class OrderApplication {
	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}
}
