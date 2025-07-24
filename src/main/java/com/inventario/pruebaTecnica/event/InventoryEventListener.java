package com.inventario.pruebaTecnica.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import com.inventario.pruebaTecnica.event.InventoryChangedEvent;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
@Slf4j
public class InventoryEventListener {

    private static final DateTimeFormatter formatter = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                        .withZone(ZoneId.systemDefault());

    @Async
    @EventListener
    public void handleInventoryChanged(InventoryChangedEvent event) {
        // Log principal estructurado
        log.info("""
            ================================================
            INVENTORY CHANGE EVENT RECEIVED
            ------------------------------------------------
            Timestamp:      {}
            Event Source:   {}
            Change Type:    {}
            Product ID:     {}
            Change Amount:  {}
            Current Stock:  {}
            Previous Stock: {}
            Change Reason:  {}
            ================================================
            """,
            formatter.format(Instant.now()),
            event.getSource().getClass().getSimpleName(),
            event.getChangeType(),
            event.getInventory().getProductId(),
            event.getInventory().getQuantity(),
            event.getChangeReason()
        );

        // Log condicional para cambios críticos
        if (event.getInventory().getQuantity() < 10) {
            log.warn("⚠️ LOW STOCK WARNING - Product {} is running low ({} units remaining)",
                event.getInventory().getProductId(),
                event.getInventory().getQuantity());
        }

        // Confirmación final
        log.info("✅ Inventory change event processed successfully for product {}", 
            event.getInventory().getProductId());
    }
}