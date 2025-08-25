package com.feritbilgi.customer_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {"com.feritbilgi"})
public class CustomerServiceApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(CustomerServiceApplication.class, args);
    }
}
