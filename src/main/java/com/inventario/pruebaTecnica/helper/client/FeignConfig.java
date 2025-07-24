package com.inventario.pruebaTecnica.helper.client;

import feign.Request;
import feign.RequestInterceptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import java.util.concurrent.TimeUnit;

public class FeignConfig {

    @Value("${product.service.api-key}")
    private String productServiceApiKey;

    // Timeout configurations
    @Value("${feign.client.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${feign.client.read-timeout:10000}")
    private int readTimeout;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
            requestTemplate.header("X-API-Key", productServiceApiKey);
        };
    }

    /**
     * Configures timeout settings for Feign client
     * 
     * @return Request.Options with configured timeouts
     */
    @Bean
    public Request.Options options() {
        return new Request.Options(
                connectTimeout, TimeUnit.MILLISECONDS,
                readTimeout, TimeUnit.MILLISECONDS,
                true // Follow redirects
        );
    }
}