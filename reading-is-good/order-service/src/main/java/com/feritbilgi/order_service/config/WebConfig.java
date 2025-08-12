package com.feritbilgi.order_service.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebConfig {

    @Bean
    @LoadBalanced
    public WebClient webClient(){
        return WebClient.builder().build(); // It creates bean in type of WebClient name of webClient
    }

    //@LoadBalanced annotation'ı farklı portlar üzerinden bizim girdiğimiz url'i arar.
    //For example: ./8084/api/inventory, ./8085/api/inventory, ./8086/api/inventory, etc.
}
