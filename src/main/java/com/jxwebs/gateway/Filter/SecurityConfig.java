package com.jxwebs.gateway.Filter;

import com.jxwebs.gateway.Common.JsonWebTokenUtility;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;

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
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("https://jxwebs.com"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))
            .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
            .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                    .pathMatchers("/api/login/**").permitAll()
                    .pathMatchers("/api/blog/**").permitAll()
                    .pathMatchers("/api/writeblog/**").hasAuthority("boss")
                    .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll() // 允許 OPTIONS
                    .anyExchange().authenticated()
            )
            .addFilterAt(new JwtAuthenticationFilter(jwtUtility), SecurityWebFiltersOrder.AUTHENTICATION)
            .build();
}

}
