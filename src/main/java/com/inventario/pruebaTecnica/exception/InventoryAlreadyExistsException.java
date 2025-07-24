package com.inventario.pruebaTecnica.exception;

import org.springframework.http.HttpStatus;

public class InventoryAlreadyExistsException extends CustomException {

    private static final long serialVersionUID = 1L;

    public InventoryAlreadyExistsException(String message) {
        super(message, HttpStatus.BAD_REQUEST,"INSUFFICIENT_STOCK" );
    }

    public InventoryAlreadyExistsException(String message, Throwable cause) {
        super(message, HttpStatus.CONFLICT,"INVENTORY_ALREADY_EXISTS");
    }
    
}
