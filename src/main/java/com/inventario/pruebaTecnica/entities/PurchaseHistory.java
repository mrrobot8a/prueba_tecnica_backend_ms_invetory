package com.inventario.pruebaTecnica.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "purchase_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "product_id", nullable = false)
    @NotNull(message = "Product ID is required")
    private UUID productId;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Unit price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Formula("quantity * unit_price") // Calculado automáticamente por Hibernate
    @Column(name = "total_amount", precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @CreationTimestamp
    @Column(name = "purchase_date", updatable = false)
    private LocalDateTime purchaseDate;

    @Column(name = "notes")
    private String notes;

    // Método para calcular el total manualmente (opcional)
    public BigDecimal calculateTotal() {
        if (unitPrice == null || quantity == null) {
            return BigDecimal.ZERO;
        }
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}