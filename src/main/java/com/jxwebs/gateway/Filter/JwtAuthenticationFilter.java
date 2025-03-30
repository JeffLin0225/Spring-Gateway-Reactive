package com.jxwebs.gateway.Filter;

import com.jxwebs.gateway.Common.JsonWebTokenUtility;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class JwtAuthenticationFilter implements WebFilter {
    private final JsonWebTokenUtility jwtUtility;

    public JwtAuthenticationFilter(JsonWebTokenUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            return jwtUtility.validateToken(token)
                    .flatMap(claims -> {
                        if (claims != null) {
                            String username = claims[0];
                            String authority = claims[1];
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            username, null, Collections.singletonList(new SimpleGrantedAuthority(authority))
                                    );
                            SecurityContext securityContext = new SecurityContextImpl(authenticationToken);
                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                        }
                        // Token 無效，返回 401
                        return unauthorizedResponse(exchange, "Invalid token");
                    })
                    .switchIfEmpty(unauthorizedResponse(exchange, "Invalid token"));
        }

        // 檢查是否需要認證的路徑
        String path = exchange.getRequest().getPath().toString();
        if (path.startsWith("/api/login") || path.startsWith("/api/blog") || exchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            // 公開路徑或 OPTIONS 請求，放行
            return chain.filter(exchange);
        }

        // 沒有 token 且需要認證，返回 401
        System.out.println("沒有給token");
        return unauthorizedResponse(exchange, "Missing token");
    }

    private Mono<Void> unauthorizedResponse(ServerWebExchange exchange, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"error\": \"Unauthorized\", \"message\": \"" + message + "\"}";
        return response.writeWith(Mono.just(response.bufferFactory().wrap(body.getBytes())));
    }
}