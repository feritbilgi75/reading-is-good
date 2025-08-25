package com.feritbilgi.api_gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity){

        serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable) // Disable CORS because we use different filter.
                .authorizeExchange(exchange ->
                        exchange
                                .pathMatchers("/auth/**").permitAll() // Login/register endpoints are open
                                .pathMatchers("/eureka/**").permitAll() // Eureka dashboard are open
                                .pathMatchers("/actuator/**").permitAll() // Actuator endpoints are open for debugging
                                .pathMatchers("/api/**").authenticated() // API endpoints require authentication
                                .anyExchange().authenticated() // other things require authentication
                )
                .oauth2ResourceServer(spec -> 
                        spec.jwt(jwt -> 
                                jwt.jwkSetUri("http://localhost:8181/realms/spring-boot-microservices-realm/protocol/openid-connect/certs")
                        ) // Look again
                );
        
        return serverHttpSecurity.build();
    }
}
