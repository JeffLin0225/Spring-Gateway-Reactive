package com.jxwebs.gateway.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthGatewayFilterFactory jwtAuthGatewayFilterFactory;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_route", r -> r.path("/auth/**")
                        .uri("http://localhost:8081"))  // 認證服務地址
                .route("user_route", r -> r.path("/user/**")
                        .filters(f -> f.filter(jwtAuthGatewayFilterFactory.apply(new Object())))  // 使用過濾器工廠
                        .uri("http://localhost:8082"))  // 用戶服務地址
                .build();
    }
}

