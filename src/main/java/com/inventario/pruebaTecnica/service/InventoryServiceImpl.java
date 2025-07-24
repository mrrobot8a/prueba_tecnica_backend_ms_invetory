package com.inventario.pruebaTecnica.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.pruebaTecnica.dtos.InventoryDTO;
import com.inventario.pruebaTecnica.dtos.InventoryResponseDTO;
import com.inventario.pruebaTecnica.dtos.ProductResponse;
import com.inventario.pruebaTecnica.dtos.PurchaseRequestDTO;
import com.inventario.pruebaTecnica.entities.Inventory;
import com.inventario.pruebaTecnica.entities.PurchaseHistory;
import com.inventario.pruebaTecnica.event.InventoryChangedEvent;
import com.inventario.pruebaTecnica.exception.InsufficientStockException;
import com.inventario.pruebaTecnica.exception.InventoryAlreadyExistsException;
import com.inventario.pruebaTecnica.exception.InventoryNotFoundException;
import com.inventario.pruebaTecnica.exception.ProductNotFoundException;
import com.inventario.pruebaTecnica.helper.client.ProductServiceClient;
import com.inventario.pruebaTecnica.mapping.InventoryMapper;
import com.inventario.pruebaTecnica.repository.InventoryRepository;
import com.inventario.pruebaTecnica.repository.PurchaseHistoryRepository;
import com.inventario.pruebaTecnica.service.useCase.InventoryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final PurchaseHistoryRepository purchaseRepository;
    private final ProductServiceClient productServiceClient;
    private final InventoryMapper inventoryMapper;
    private final ApplicationEventPublisher eventPublisher;
    @Value("${product.service.api-key}")
    private String productServiceApiKey;

    @Override
    @Transactional(readOnly = true)
    public InventoryResponseDTO getInventoryByProductId(String productId)
            throws InventoryNotFoundException {
        try {
            UUID productUuid = UUID.fromString(productId);
            Inventory inventory = inventoryRepository.findByProductId(productUuid)
                    .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));

            // 2. Obtener información del producto
            ProductResponse product = productServiceClient.getProductById(productUuid, productServiceApiKey);
            if (product == null) {
                throw new ProductNotFoundException("Product not found with ID: " + productId);
            }
            // 3. Mapear la respuesta combinada
            InventoryResponseDTO response = inventoryMapper.toResponseDto(inventory);
            response.getData().setProduct(inventoryMapper.toProductAttributes(product));

            return response;

        } catch (IllegalArgumentException e) {
            throw new InventoryNotFoundException(
                    "Formato de ID inválido. Se esperaba un UUID válido para: " + productId);
        }
    }

    @Override
    @Transactional
    public InventoryResponseDTO processPurchase(PurchaseRequestDTO request)
            throws ProductNotFoundException, InsufficientStockException, InventoryNotFoundException {

        ProductResponse product = validateProductExists(request.getProductId());
        Inventory inventory = getValidInventory(request.getProductId(), request.getQuantity());

        PurchaseHistory purchase = PurchaseHistory.builder()
                .productId(request.getProductId())
                .quantity(request.getQuantity())
                .unitPrice(product.getPrice())
                .notes(inventory.getNotes())
                .build();

        int previousQuantity = inventory.getQuantity();
        inventory.setQuantity(previousQuantity - request.getQuantity());

        PurchaseHistory savedPurchase = purchaseRepository.save(purchase);
        Inventory updatedInventory = inventoryRepository.save(inventory);

        eventPublisher.publishEvent(new InventoryChangedEvent(
                this,
                updatedInventory,
                InventoryChangedEvent.ChangeType.STOCK_DECREASE,
                "Purchase processed for product " + request.getProductId(), previousQuantity - request.getQuantity()));

        InventoryResponseDTO response = inventoryMapper.toResponseDto(updatedInventory);
        response.getData().setPurchase(inventoryMapper.toPurchaseAttributes(savedPurchase));
        response.getData().setProduct(inventoryMapper.toProductAttributes(product));

        return response;
    }

    @Override
    @Transactional
    public InventoryResponseDTO createInventory(InventoryDTO inventoryDTO)
            throws ProductNotFoundException {
        try {
            validateProductExists(inventoryDTO.getProductId());

            inventoryRepository.findByProductId(inventoryDTO.getProductId())
                    .ifPresent(existing -> {
                        throw new IllegalStateException("❌ Ya existe inventario para este producto");
                    });

            Inventory newInventory = createNewInventory(inventoryDTO);

            eventPublisher.publishEvent(new InventoryChangedEvent(
                    this,
                    newInventory,
                    InventoryChangedEvent.ChangeType.PRODUCT_ADDED,
                    "New product added to inventory", 0));

            return inventoryMapper.toResponseDto(inventoryRepository.save(newInventory));

        } catch (Exception e) {
            throw new InventoryAlreadyExistsException(
                    "❌ Ya existe inventario para este producto: " + inventoryDTO.getProductId(), e);
        }

    }

    @Override
    @Transactional
    public InventoryResponseDTO updateInventory(InventoryDTO inventoryDTO)
            throws ProductNotFoundException, InventoryNotFoundException {
        validateProductExists(inventoryDTO.getProductId());

        Inventory existingInventory = inventoryRepository.findByProductId(inventoryDTO.getProductId())
                .orElseThrow(() -> new IllegalStateException("❌ No existe inventario para este producto"));

        Inventory updated = updateExistingInventory(existingInventory, inventoryDTO);

        eventPublisher.publishEvent(new InventoryChangedEvent(
                this,
                updated,
                InventoryChangedEvent.ChangeType.PRODUCT_UPDATED,
                "Manual inventory update", 0));

        return inventoryMapper.toResponseDto(inventoryRepository.save(updated));

    }

    private ProductResponse validateProductExists(UUID productId) throws ProductNotFoundException {
        if (productServiceClient.productExists(productId, productServiceApiKey) == null) {
            throw new ProductNotFoundException("Product not found: " + productId);
        }
        return productServiceClient.getProductById(productId, productServiceApiKey);
    }

    private Inventory getValidInventory(UUID productId, int requestedQuantity)
            throws InventoryNotFoundException, InsufficientStockException {

        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getQuantity() < requestedQuantity) {
            throw new InsufficientStockException(
                    String.format("Insufficient stock. Available: %d, Requested: %d",
                            inventory.getQuantity(), requestedQuantity));
        }

        return inventory;
    }

    @Override
    public List<InventoryResponseDTO> getLowStockItems(int threshold) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getLowStockItems'");
    }

    private Inventory updateExistingInventory(Inventory existing, InventoryDTO dto) {
        existing.setQuantity(dto.getQuantity());
        return existing;
    }

    private Inventory createNewInventory(InventoryDTO dto) {
        return inventoryMapper.toEntity(dto);
    }

}