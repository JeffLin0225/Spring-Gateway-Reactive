package com.jxwebs.gateway.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth_route", r -> r.path("/serviceAtest/**")
                        .uri("http://localhost:8090")) // 認證服務地址
                .route("user_route", r -> r.path("/serviceBtest/**")
//                        .filters(f -> f.filter(jwtAuthGatewayFilterFactory.apply(new Object())))  // 使用過濾器工廠
                        .uri("http://localhost:8091"))  // 用戶服務地址
                .build();
    }
}

