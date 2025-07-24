package com.inventario.pruebaTecnica.exception;

import org.springframework.http.HttpStatus;

public class InventoryNotFoundException extends CustomException {
    public InventoryNotFoundException(String productId) {
        super("Inventory not found for product: " + productId, 
              HttpStatus.NOT_FOUND, 
              "INVENTORY_NOT_FOUND");
    }
}