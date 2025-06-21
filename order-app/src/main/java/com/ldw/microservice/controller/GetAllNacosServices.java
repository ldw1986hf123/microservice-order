package com.ldw.microservice.controller;
import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.ListView;
import com.alibaba.nacos.api.naming.pojo.ServiceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class GetAllNacosServices {
    public static void main(String[] args) {
        // Nacos 服务器地址
        String serverAddr = "192.168.172.129:8848"; // 修改为你的 Nacos 服务器地址
        String namespace = "dev"; // 命名空间，默认 public
        String groupName = "DEFAULT_GROUP"; // 服务分组，默认 DEFAULT_GROUP

        // 配置 Nacos 属性
        Properties properties = new Properties();
        properties.put("serverAddr", serverAddr);
        properties.put("namespace", namespace);

        try {
            // 创建 NamingService 实例
            NamingService namingService = NacosFactory.createNamingService(properties);

            // 获取所有服务信息
            List<ServiceInfo> allServices = getAllServices(namingService, groupName);

            // 输出所有服务信息
            if (allServices.isEmpty()) {
                System.out.println("没有注册的服务");
            } else {
                for (ServiceInfo service : allServices) {
                    System.out.println("服务名称: " + service.getName());
                    System.out.println("服务分组: " + service.getGroupName());
                    System.out.println("实例数: " + service.getHosts().size());
                    System.out.println("实例详情: " + service.getHosts());
                    System.out.println("-------------------");
                }
                System.out.println("总服务数: " + allServices.size());
            }

        } catch (NacosException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取所有注册的服务信息
     * @param namingService NamingService 实例
     * @param groupName 服务分组名称
     * @return 所有服务信息列表
     * @throws NacosException Nacos 异常
     */
    private static List<ServiceInfo> getAllServices(NamingService namingService, String groupName) throws NacosException {
        List<ServiceInfo> allServices = new ArrayList<>();
        int pageNo = 1; // 从第1页开始
        int pageSize = 100; // 每页获取的服务数量，可根据需求调整
        ListView<String> currentPageServices;

        do {
            // 获取当前页的服务列表
            currentPageServices = namingService.getServicesOfServer(pageNo, pageSize, groupName);

        } while (currentPageServices != null  ); // 当返回的服务数量等于 pageSize 时，可能还有更多数据

        return allServices;
    }
}