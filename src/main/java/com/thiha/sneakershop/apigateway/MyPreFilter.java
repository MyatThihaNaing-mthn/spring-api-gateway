package com.thiha.sneakershop.apigateway;

import org.apache.hc.client5.http.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.thiha.sneakershop.apigateway.services.JwtService;
import com.thiha.sneakershop.apigateway.services.RouterValidator;

import reactor.core.publisher.Mono;

@Component
public class MyPreFilter implements GlobalFilter, Ordered{
    final Logger logger = LoggerFactory.getLogger(MyPreFilter.class);
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RouterValidator routerValidator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        logger.info("Auth filter..");
        if(routerValidator.isSecured.test(exchange.getRequest())){
            String token = null;
            try{
                token = getTokenFromRequest(exchange.getRequest());
            }catch(Exception e){
                logger.error("authentication error", e);
                return handleUnauthorizedRequest(exchange);
            }
            try{
                if(jwtService.isTokenExpired(token)){
                    // return unauthorized response
                    return handleUnauthorizedRequest(exchange);
                }
            }catch(Exception e){
                logger.error("authentication error", e);
                return handleUnauthorizedRequest(exchange);
            }
        }
        return chain.filter(exchange);
    }

    private String getTokenFromRequest(ServerHttpRequest serverHttpRequest) throws AuthenticationException{
        if(!serverHttpRequest.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
            throw new AuthenticationException("authorization header missing");
        }
        String authHeader = serverHttpRequest.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }else{
            throw new AuthenticationException("Authentication error");
        }
    }

    private Mono<Void> handleUnauthorizedRequest(ServerWebExchange exchange){
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }
  
    @Override
    public int getOrder() {
        return 0;
    }
}
