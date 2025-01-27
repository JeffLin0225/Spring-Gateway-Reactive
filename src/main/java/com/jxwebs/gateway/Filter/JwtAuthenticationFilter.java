package com.jxwebs.gateway.Filter;

import com.jxwebs.gateway.Common.JsonWebTokenUtility;
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
//    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JsonWebTokenUtility jwtUtility) {
        this.jwtUtility = jwtUtility;
//        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 验证 Token 并提取用户名和权限
            return jwtUtility.validateToken(token)
                    .flatMap(claims -> {
                        if (claims != null) {
                            System.out.println("有認證: "+"claims[0]"+claims[0]+" , claims[1]"+claims[1]);
                            String username = claims[0];
                            String authority = claims[1];  // 假設 role 是權限字串

                            // 創建認證令牌
                            UsernamePasswordAuthenticationToken authenticationToken =
                                    new UsernamePasswordAuthenticationToken(
                                            username, null, Collections.singletonList(new SimpleGrantedAuthority(authority))
                                    );

                            // 创建 SecurityContext
                            SecurityContext securityContext = new SecurityContextImpl(authenticationToken);

                            // 将 SecurityContext 设置到上下文中
                            return chain.filter(exchange)
                                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
                        }
                        return Mono.empty(); // 无效 Token
                    });
        }
        System.out.println("沒有token");
        // 无 Authorization 头，直接通过过滤器链
        return chain.filter(exchange);

    }
}