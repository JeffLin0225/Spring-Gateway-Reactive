package com.jxwebs.gateway.Filter;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        System.out.println("有盡GatewayConfig");
        return builder.routes()
                .route("secure_route", r -> r.path("/api/login/**")
                        .uri("http://localhost:8087")) // 認證服務地址
                .route("user_route", r -> r.path("/api/writeblog/**","/api/blog/**")
//                        .filters(f -> f.filter(jwtAuthGatewayFilterFactory.apply(new Object())))  // 使用過濾器工廠
                        .uri("http://localhost:8081"))  // blog
                .build();
    }
}

