package com.inventario.pruebaTecnica.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.inventario.pruebaTecnica.dtos.InventoryDTO;
import com.inventario.pruebaTecnica.dtos.InventoryResponseDTO;
import com.inventario.pruebaTecnica.dtos.ProductResponse;
import com.inventario.pruebaTecnica.dtos.PurchaseRequestDTO;
import com.inventario.pruebaTecnica.entities.Inventory;
import com.inventario.pruebaTecnica.entities.PurchaseHistory;
import com.inventario.pruebaTecnica.event.InventoryChangedEvent;
import com.inventario.pruebaTecnica.exception.*;
import com.inventario.pruebaTecnica.helper.client.ProductServiceClient;
import com.inventario.pruebaTecnica.mapping.InventoryMapper;
import com.inventario.pruebaTecnica.repository.InventoryRepository;
import com.inventario.pruebaTecnica.repository.PurchaseHistoryRepository;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private PurchaseHistoryRepository purchaseRepository;

    @Mock
    private ProductServiceClient productServiceClient;

    @Mock
    private InventoryMapper inventoryMapper;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private InventoryServiceImpl inventoryService;

    private final UUID testProductId = UUID.randomUUID();
    private final String testApiKey = "test-api-key";

    @BeforeEach
    void setUp() {
        inventoryService = new InventoryServiceImpl(
            inventoryRepository,
            purchaseRepository,
            productServiceClient,
            inventoryMapper,
            eventPublisher
        );
    }

    @Test
    void getInventoryByProductId_ShouldReturnInventory_WhenExists() {
        // Arrange
        Inventory inventory = new Inventory();
        inventory.setProductId(testProductId);
        
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(testProductId);
        productResponse.setName("Test Product");

        when(inventoryRepository.findByProductId(testProductId))
            .thenReturn(Optional.of(inventory));
        when(productServiceClient.getProductById(testProductId, testApiKey))
            .thenReturn(productResponse);
        when(inventoryMapper.toResponseDto(inventory))
            .thenReturn(new InventoryResponseDTO());

        // Act
        InventoryResponseDTO result = inventoryService.getInventoryByProductId(testProductId.toString());

        // Assert
        assertNotNull(result);
        verify(inventoryRepository).findByProductId(testProductId);
        verify(productServiceClient).getProductById(testProductId, testApiKey);
    }

    @Test
    void getInventoryByProductId_ShouldThrow_WhenInvalidUUID() {
        assertThrows(InventoryNotFoundException.class, () -> {
            inventoryService.getInventoryByProductId("invalid-uuid");
        });
    }

    @Test
    void processPurchase_ShouldUpdateInventory_WhenValidRequest() {
        // Arrange
        int quantity = 5;
        PurchaseRequestDTO request = new PurchaseRequestDTO();
        request.setProductId(testProductId);
        request.setQuantity(quantity);

        Inventory inventory = new Inventory();
        inventory.setProductId(testProductId);
        inventory.setQuantity(10);
        
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(testProductId);
        productResponse.setPrice(BigDecimal.valueOf(100));

        when(productServiceClient.productExists(testProductId, testApiKey))
            .thenReturn(productResponse);
        when(inventoryRepository.findByProductId(testProductId))
            .thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(any(Inventory.class)))
            .thenReturn(inventory);
        when(purchaseRepository.save(any(PurchaseHistory.class)))
            .thenReturn(new PurchaseHistory());

        // Act
        InventoryResponseDTO result = inventoryService.processPurchase(request);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository).save(argThat(i -> i.getQuantity() == 5));
        verify(eventPublisher).publishEvent(any(InventoryChangedEvent.class));
    }

    @Test
    void processPurchase_ShouldThrow_WhenInsufficientStock() {
        // Arrange
        PurchaseRequestDTO request = new PurchaseRequestDTO();
        request.setProductId(testProductId);
        request.setQuantity(15); // Más que el stock disponible

        Inventory inventory = new Inventory();
        inventory.setProductId(testProductId);
        inventory.setQuantity(10);

        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(testProductId);

        when(productServiceClient.productExists(testProductId, testApiKey))
            .thenReturn(productResponse);
        when(inventoryRepository.findByProductId(testProductId))
            .thenReturn(Optional.of(inventory));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> {
            inventoryService.processPurchase(request);
        });
    }

    @Test
    void createInventory_ShouldCreateNew_WhenProductNotExists() {
        // Arrange
        InventoryDTO dto = new InventoryDTO();
        dto.setProductId(testProductId);
        dto.setQuantity(10);

        when(inventoryRepository.findByProductId(testProductId))
            .thenReturn(Optional.empty());
        when(productServiceClient.productExists(testProductId, testApiKey))
            .thenReturn(new ProductResponse());
        when(inventoryMapper.toEntity(dto))
            .thenReturn(new Inventory());
        when(inventoryRepository.save(any(Inventory.class)))
            .thenReturn(new Inventory());

        // Act
        InventoryResponseDTO result = inventoryService.createInventory(dto);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository).save(any(Inventory.class));
        verify(eventPublisher).publishEvent(any(InventoryChangedEvent.class));
    }

    @Test
    void createInventory_ShouldThrow_WhenInventoryExists() {
        // Arrange
        InventoryDTO dto = new InventoryDTO();
        dto.setProductId(testProductId);

        when(inventoryRepository.findByProductId(testProductId))
            .thenReturn(Optional.of(new Inventory()));
        when(productServiceClient.productExists(testProductId, testApiKey))
            .thenReturn(new ProductResponse());

        // Act & Assert
        assertThrows(InventoryAlreadyExistsException.class, () -> {
            inventoryService.createInventory(dto);
        });
    }

    @Test
    void updateInventory_ShouldUpdate_WhenInventoryExists() {
        // Arrange
        InventoryDTO dto = new InventoryDTO();
        dto.setProductId(testProductId);
        dto.setQuantity(20);

        Inventory existing = new Inventory();
        existing.setProductId(testProductId);
        existing.setQuantity(10);

        when(inventoryRepository.findByProductId(testProductId))
            .thenReturn(Optional.of(existing));
        when(productServiceClient.productExists(testProductId, testApiKey))
            .thenReturn(new ProductResponse());
        when(inventoryRepository.save(any(Inventory.class)))
            .thenReturn(existing);

        // Act
        InventoryResponseDTO result = inventoryService.updateInventory(dto);

        // Assert
        assertNotNull(result);
        verify(inventoryRepository).save(argThat(i -> i.getQuantity() == 20));
    }

    @Test
    void updateInventory_ShouldThrow_WhenInventoryNotFound() {
        // Arrange
        InventoryDTO dto = new InventoryDTO();
        dto.setProductId(testProductId);

        when(inventoryRepository.findByProductId(testProductId))
            .thenReturn(Optional.empty());
        when(productServiceClient.productExists(testProductId, testApiKey))
            .thenReturn(new ProductResponse());

        // Act & Assert
        assertThrows(InventoryNotFoundException.class, () -> {
            inventoryService.updateInventory(dto);
        });
    }
}