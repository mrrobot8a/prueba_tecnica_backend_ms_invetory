package com.inventario.pruebaTecnica.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory", uniqueConstraints = @UniqueConstraint(columnNames = "product_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "product_id", nullable = false, unique = true)
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity must be zero or positive")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal unitPrice;

    @Size(max = 255, message = "Notes must not exceed 255 characters")
    @NotBlank(message = "Notes cannot be blank")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void deductStock(@Min(1) Integer amount) {
        if (this.quantity < amount) {
            throw new IllegalStateException("Stock insuficiente");
        }
        this.quantity -= amount;
    }

    public Object getCategory() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCategory'");
    }
}