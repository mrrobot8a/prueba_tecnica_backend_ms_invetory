package com.inventario.pruebaTecnica.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryResponseDTO {

    @JsonProperty("data")
    private InventoryData data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryData {
        private String type;
        private String id;
        private InventoryAttributes inventoryAttributes;
        private PurchaseAttributes purchase;
        private ProductAttributes product;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryAttributes {
        @JsonProperty("product_id")
        private String productId;

        @JsonProperty("available_quantity")
        private Integer availableQuantity;

        @JsonProperty("last_updated")
        private LocalDateTime lastUpdated;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductAttributes {
        @JsonProperty("product_id")
        private String productId;

        @JsonProperty("product_name")
        private String productName;

        @JsonProperty("product_description")
        private String description;

        @JsonProperty("product_price")
        private BigDecimal price;

        @JsonProperty("product_category")
        private String category;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PurchaseAttributes {
        @JsonProperty("purchase_id")
        private Long purchaseId;

        @JsonProperty("quantity_purchased")
        private Integer quantity;

        @JsonProperty("unit_price")
        private BigDecimal unitPrice;

        @JsonProperty("total_amount")
        private BigDecimal totalAmount;

        @JsonProperty("purchase_date")
        private LocalDateTime purchaseDate;
    }
}