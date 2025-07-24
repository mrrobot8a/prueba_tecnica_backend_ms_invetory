package com.inventario.pruebaTecnica.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import java.math.BigDecimal;
import com.inventario.pruebaTecnica.dtos.InventoryDTO;
import com.inventario.pruebaTecnica.dtos.InventoryResponseDTO;
import com.inventario.pruebaTecnica.dtos.ProductResponse;
import com.inventario.pruebaTecnica.entities.Inventory;
import com.inventario.pruebaTecnica.entities.PurchaseHistory;

@Mapper(componentModel = "spring")
public interface InventoryMapper {

    InventoryMapper INSTANCE = Mappers.getMapper(InventoryMapper.class);

    InventoryDTO toDto(Inventory inventory);

    @Mapping(target = "id", ignore = true)
    Inventory toEntity(InventoryDTO inventoryDTO); 

    @Mapping(target = "data.type", constant = "inventory")
    @Mapping(target = "data.id", source = "productId")
    @Mapping(target = "data.inventoryAttributes.productId", source = "productId")
    @Mapping(target = "data.inventoryAttributes.availableQuantity", source = "quantity")
    @Mapping(target = "data.inventoryAttributes.lastUpdated", source = "updatedAt")
    @Mapping(target = "data.purchase", ignore = true)
    InventoryResponseDTO toResponseDto(Inventory inventory);

    @Mapping(target = "productId", source = "id")
    @Mapping(target = "productName", source = "name")
    InventoryResponseDTO.ProductAttributes toProductAttributes(ProductResponse product);

    @Mapping(target = "purchaseId", source = "id")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "totalAmount", expression = "java(purchase.getUnitPrice().multiply(java.math.BigDecimal.valueOf(purchase.getQuantity())))")
    InventoryResponseDTO.PurchaseAttributes toPurchaseAttributes(PurchaseHistory purchase);
}