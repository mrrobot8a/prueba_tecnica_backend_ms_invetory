package com.inventario.pruebaTecnica.exception;

import org.springframework.http.HttpStatus;

public class InventoryUpdateException extends CustomException {
    public InventoryUpdateException(String message) {
        super(message, 
              HttpStatus.CONFLICT, 
              "INVENTORY_UPDATE_FAILED");
    }
}