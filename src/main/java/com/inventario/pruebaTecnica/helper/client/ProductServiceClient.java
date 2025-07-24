package com.inventario.pruebaTecnica.helper.client;

import com.inventario.pruebaTecnica.dtos.ProductResponse;
import com.inventario.pruebaTecnica.exception.ProductNotFoundException;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

/**
 * Feign client interface for communicating with Product Service
 * 
 * <p>
 * Handles all HTTP requests to the Product microservice with proper timeout
 * configuration and error handling.
 * </p>
 */
@FeignClient(name = "product-service", url = "${product.service.url}", configuration = FeignConfig.class)
public interface ProductServiceClient {

    /**
     * Retrieves product details by ID
     * 
     * @param id     Product UUID
     * @param apiKey Service-to-service authentication key
     * @return Product details wrapped in ResponseEntity
     */
    @GetMapping("/api/v1/product/get-by-id/{id}")
    ProductResponse getProductById(
            @PathVariable UUID id,
            @RequestHeader("X-API-Key") String apiKey);

    /**
     * Retrieves all active products
     * 
     * @param apiKey Service-to-service authentication key
     * @return List of products
     */
    @GetMapping("/api/v1/product/get-all")
    List<ProductResponse> getAllProducts(
            @RequestHeader("X-API-Key") String apiKey);

    /**
     * Checks if product exists (default implementation)
     * 
     * @param id     Product UUID
     * @param apiKey Service-to-service authentication key
     * @return true if product exists and is active
     */
    default ProductResponse productExists(UUID id, String apiKey) {
        try {
            ProductResponse product = getProductById(id, apiKey);

            if (product != null && product.isActive()) {
                return product;
            } else {
                throw new ProductNotFoundException("Product is not active or does not exist for ID: " + id);
            }
        } catch (Exception e) {
            throw e;
        }
    }
}
