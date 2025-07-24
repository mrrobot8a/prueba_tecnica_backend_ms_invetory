package com.inventario.pruebaTecnica.dtos;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.UUID;

@Data
public class InventoryRequestDTO {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @PositiveOrZero(message = "Quantity must be zero or positive")
    private Integer quantity;

    @NotBlank(message = "Movement type is required")
    @Pattern(regexp = "^(ENTRADA|SALIDA)$", 
             message = "Movement type must be ENTRADA or SALIDA")
    private String movementType;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Size(max = 255, message = "Notes must not exceed 255 characters")
    private String notes;
}