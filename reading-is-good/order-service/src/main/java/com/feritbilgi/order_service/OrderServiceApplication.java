package com.feritbilgi.order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.feritbilgi"})
public class OrderServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
