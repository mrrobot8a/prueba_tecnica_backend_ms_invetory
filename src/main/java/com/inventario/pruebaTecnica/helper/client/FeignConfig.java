package com.inventario.pruebaTecnica.helper.client;

import feign.Request;
import feign.RequestInterceptor;
import feign.RetryableException;
import feign.Retryer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;
@Configuration
public class FeignConfig {

    @Value("${product.service.api-key}")
    private String productServiceApiKey;

    // Timeout configurations
    @Value("${feign.client.connect-timeout:5000}")
    private int connectTimeout;

    @Value("${feign.client.read-timeout:10000}")
    private int readTimeout;

    // Retry configurations
    @Value("${feign.client.retry-period:1000}")
    private long period;

    @Value("${feign.client.retry-max-period:5000}")
    private long maxPeriod;

    @Value("${feign.client.retry-max-attempts:3}")
    private int maxAttempts;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("Content-Type", "application/json");
            requestTemplate.header("Accept", "application/json");
            requestTemplate.header("X-API-Key", productServiceApiKey);
        };
    }

    @Bean
    public Request.Options options() {
        return new Request.Options(
                connectTimeout, TimeUnit.MILLISECONDS,
                readTimeout, TimeUnit.MILLISECONDS,
                true);
    }

    /**
     * Configures retry logic for Feign client
     */
    @Bean
    public Retryer retryer() {
        return new Retryer.Default(1000, 5000, 3) {
            private int attempt = 1;

            @Override
            public void continueOrPropagate(RetryableException e) {
                System.out.println("🔁 Intento #" + attempt + " fallido: " + e.getMessage());
                attempt++;
                super.continueOrPropagate(e);
            }

            @Override
            public Retryer clone() {
                return new Retryer.Default(1000, 5000, 3);
            }
        };
    }

}
