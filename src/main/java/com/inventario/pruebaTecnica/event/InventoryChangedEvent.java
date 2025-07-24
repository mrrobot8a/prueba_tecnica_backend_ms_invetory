package com.inventario.pruebaTecnica.event;

import com.inventario.pruebaTecnica.entities.Inventory;
import com.inventario.pruebaTecnica.service.InventoryServiceImpl;

import org.springframework.context.ApplicationEvent;

public class InventoryChangedEvent extends ApplicationEvent {
    private final Inventory inventory;
    private final ChangeType changeType;
    private final String changeReason;
    private final int changeAmount;  

    public InventoryChangedEvent(Object source, Inventory inventory, 
                               ChangeType changeType, String changeReason,
                               int changeAmount) {
        super(source);
        this.inventory = inventory;
        this.changeType = changeType;
        this.changeReason = changeReason;
        this.changeAmount = changeAmount;
    }

    public InventoryChangedEvent(InventoryServiceImpl source, Inventory updatedInventory, ChangeType stockDecrease,
            String changeReason2, int changeAmount2) {
        super(source);
        this.inventory = updatedInventory;
        this.changeType = stockDecrease;
        this.changeReason = changeReason2;
        this.changeAmount = changeAmount2;
    }

    public Inventory getInventory() { return inventory; }
    public ChangeType getChangeType() { return changeType; }
    public String getChangeReason() { return changeReason; }
    public int getChangeAmount() { return changeAmount; }

    public enum ChangeType {
        STOCK_DECREASE,  
        STOCK_INCREASE,  
        PRODUCT_ADDED,   
        PRODUCT_UPDATED  
    }
}