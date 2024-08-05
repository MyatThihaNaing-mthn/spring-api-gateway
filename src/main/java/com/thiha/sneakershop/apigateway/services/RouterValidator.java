package com.thiha.sneakershop.apigateway.services;

import java.util.List;
import java.util.function.Predicate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

@Service
public class RouterValidator {
    
    private final List<String> openEndPoints = List.of(
        "/api/auth/login",
        "/api/auth/admin/register"
    );

    public Predicate<ServerHttpRequest> isSecured = 
        request -> openEndPoints
                        .stream()
                        .noneMatch(uri -> request.getURI().getPath().contains(uri));
}
