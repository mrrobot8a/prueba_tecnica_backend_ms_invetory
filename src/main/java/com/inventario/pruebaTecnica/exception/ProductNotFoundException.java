package com.inventario.pruebaTecnica.exception;

import org.springframework.http.HttpStatus;

public class ProductNotFoundException extends CustomException {
    public ProductNotFoundException(String productId) {
        super("Product not found: " + productId, 
              HttpStatus.NOT_FOUND, 
              "PRODUCT_NOT_FOUND");
    }
}
