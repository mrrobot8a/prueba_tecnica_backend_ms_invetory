package com.inventario.pruebaTecnica.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class ApiKeyAuthFilter extends OncePerRequestFilter {

    private final String apiKeyHeader;
    private final String apiKeyValue;

    public ApiKeyAuthFilter(
            @Value("${app.security.api-key-header}") String apiKeyHeader,
            @Value("${app.security.api-key}") String apiKeyValue) {
        this.apiKeyHeader = apiKeyHeader;
        this.apiKeyValue = apiKeyValue;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        
        String apiKey = request.getHeader(apiKeyHeader);
         
        System.out.println("Header recibido: " + apiKeyHeader + "=" + apiKey); // Debug
        System.out.println("Clave esperada: " + apiKeyValue); // Debug

        if (apiKey != null && apiKey.equals(apiKeyValue)) {
            // Autenticación exitosa
            System.out.println("Autenticación exitosa");
        } else {
            System.out.println("Autenticación fallida");
        }

        if (apiKey != null && apiKey.equals(apiKeyValue)) {
            var auth = new ApiKeyAuthentication(apiKey, AuthorityUtils.createAuthorityList("API_KEY"));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        
        chain.doFilter(request, response);
    }
    
}