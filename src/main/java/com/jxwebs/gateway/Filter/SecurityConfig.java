package com.jxwebs.gateway.Filter;

import com.jxwebs.gateway.Common.JsonWebTokenUtility;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JsonWebTokenUtility jwtUtility;

    public SecurityConfig(JsonWebTokenUtility jwtUtility,JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtUtility = jwtUtility;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/serviceAtest/**").permitAll()  // 允許 /serviceAtest 路徑的請求
                        .pathMatchers("/serviceBtest/**").hasAuthority("boss")  // 需要 boss 權限的請求
                        .anyExchange().authenticated()  // 其他所有請求都需要身份驗證
                )
                .addFilterAt(new JwtAuthenticationFilter(jwtUtility),
                        SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

}
