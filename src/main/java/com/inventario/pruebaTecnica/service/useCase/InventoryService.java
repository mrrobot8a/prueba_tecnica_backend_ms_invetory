package com.inventario.pruebaTecnica.service.useCase;

import java.util.List;

import com.inventario.pruebaTecnica.dtos.InventoryDTO;
import com.inventario.pruebaTecnica.dtos.InventoryResponseDTO;
import com.inventario.pruebaTecnica.dtos.PurchaseRequestDTO;
import com.inventario.pruebaTecnica.exception.InsufficientStockException;
import com.inventario.pruebaTecnica.exception.InventoryNotFoundException;
import com.inventario.pruebaTecnica.exception.ProductNotFoundException;

public interface InventoryService {
    InventoryResponseDTO getInventoryByProductId(String productId) throws InventoryNotFoundException;
    InventoryResponseDTO processPurchase(PurchaseRequestDTO request)
            throws ProductNotFoundException, InsufficientStockException, InventoryNotFoundException;
    InventoryResponseDTO createInventory(InventoryDTO inventoryDTO) throws ProductNotFoundException;
    InventoryResponseDTO updateInventory(InventoryDTO inventoryDTO)
            throws ProductNotFoundException, InventoryNotFoundException;   
    List<InventoryResponseDTO> getLowStockItems(int threshold);
}