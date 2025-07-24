package com.inventario.pruebaTecnica.controllers;

import com.inventario.pruebaTecnica.dtos.InventoryDTO;
import com.inventario.pruebaTecnica.dtos.InventoryResponseDTO;
import com.inventario.pruebaTecnica.dtos.PurchaseRequestDTO;
import com.inventario.pruebaTecnica.exception.InventoryNotFoundException;
import com.inventario.pruebaTecnica.exception.ProductNotFoundException;
import com.inventario.pruebaTecnica.service.useCase.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventarioController {

    private final InventoryService inventarioService;

    @PostMapping("/create")
    public ResponseEntity<InventoryResponseDTO> createInventory(
            @RequestBody @Valid InventoryDTO dto) throws ProductNotFoundException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(inventarioService.createInventory(dto));
    }

    // Actualizar inventario existente (PUT)
    @PutMapping("/update")
    public ResponseEntity<InventoryResponseDTO> updateInventory(
            @RequestBody @Valid InventoryDTO dto) throws ProductNotFoundException, InventoryNotFoundException {
        return ResponseEntity.ok(inventarioService.updateInventory(dto));
    }

    @GetMapping("/get-by-product/{productId}")
    public ResponseEntity<InventoryResponseDTO> getByProductId(
            @PathVariable String productId) {
        return ResponseEntity.ok(inventarioService.getInventoryByProductId(productId));
    }

    @PostMapping("/process-purchase")
    public ResponseEntity<InventoryResponseDTO> processPurchase(
            @RequestBody @Valid PurchaseRequestDTO dto) {
        return ResponseEntity.ok(inventarioService.processPurchase(dto));
    }

    @GetMapping("/low-stock/{threshold}")
    public ResponseEntity<List<InventoryResponseDTO>> getLowStockItems(
            @PathVariable int threshold) {
        return ResponseEntity.ok(inventarioService.getLowStockItems(threshold));
    }
}
