//package com.jxwebs.gateway.Filter;
//
//import com.jxwebs.gateway.Common.JsonWebTokenUtility;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.GatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.context.ReactiveSecurityContextHolder;
//import org.springframework.security.core.context.SecurityContextImpl;
//import org.springframework.stereotype.Component;
//import reactor.core.publisher.Mono;
//
//import java.util.Collections;
//
//@Component
//public class JwtAuthGatewayFilterFactory implements GatewayFilterFactory<Object> {
//
//    private final JsonWebTokenUtility jsonWebTokenUtility;
//
//    public JwtAuthGatewayFilterFactory(JsonWebTokenUtility jsonWebTokenUtility) {
//        this.jsonWebTokenUtility = jsonWebTokenUtility;
//    }
//
//    @Override
//    public GatewayFilter apply(Object config) {
//        return (exchange, chain) -> {
//            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
//            if (authHeader != null && authHeader.startsWith("Bearer ")) {
//                String token = authHeader.substring(7);
//                return jsonWebTokenUtility.validateToken(token)
//                        .flatMap(claims -> {
//                            if (claims != null) {
//                                System.out.println("有筆對");
//                                String username = claims[0];
//                                // 假設 claims[1] 是權限字串
//                                String authority = claims[1];
//                                System.out.println("claims[0]="+claims[0]+"  ,claims[1]="+claims[1]);
//
//                                // 創建認證令牌
//                                UsernamePasswordAuthenticationToken authenticationToken =
//                                        new UsernamePasswordAuthenticationToken(
//                                                username,
//                                                null,
//                                                Collections.singletonList(new SimpleGrantedAuthority(authority))
//                                        );
//
//                                return chain.filter(exchange)
//                                        .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(
//                                                Mono.just(new SecurityContextImpl(authenticationToken))
//                                        ));
//                            } else {
//                                System.out.println("claims else  null");
//                                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                                return exchange.getResponse().setComplete();
//                            }
//                        }).onErrorResume(e -> {
//                            System.out.println("都不對");
//                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//                            return exchange.getResponse().setComplete();
//                        });
//            }
//            System.out.println("最後一種");
//            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            return exchange.getResponse().setComplete();
//        };
//    }
//
//    @Override
//    public Class<Object> getConfigClass() {
//        return Object.class;
//    }
//
//    @Override
//    public Object newConfig() {
//        return new Object();
//    }
//}
