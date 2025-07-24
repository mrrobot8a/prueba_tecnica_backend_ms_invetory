package com.inventario.pruebaTecnica.exception;

import org.springframework.http.HttpStatus;

public class InsufficientStockException extends CustomException {
    public InsufficientStockException(String message) {
        super(message, 
              HttpStatus.BAD_REQUEST, 
              "INSUFFICIENT_STOCK");
    }
}