package com.inventario.pruebaTecnica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.inventario.pruebaTecnica.entities.Inventory;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, UUID> {

    /**
     * Finds inventory item by product ID
     * @param productId Product ID to search for
     * @return Optional containing the inventory item if found
     */
    Optional<Inventory> findByProductId(UUID productId);

    /**
     * Checks if a product exists in inventory
     * @param productId Product ID to check
     * @return true if exists, false otherwise
     */
    boolean existsByProductId(UUID productId);

    /**
     * Atomically deducts stock quantity (thread-safe operation)
     * @param productId Product ID to update
     * @param amount Quantity to deduct (must be positive)
     * @return Number of affected rows (1 if successful, 0 if failed)
     */
    @Transactional
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Inventory i SET i.quantity = i.quantity - :amount WHERE i.productId = :productId AND i.quantity >= :amount")
    int deductStock(String productId, int amount);

    /**
     * Adds stock to existing inventory
     * @param productId Product ID to update
     * @param amount Quantity to add (must be positive)
     * @return Number of affected rows
     */
    @Transactional
    @Modifying
    @Query("UPDATE Inventory i SET i.quantity = i.quantity + :amount WHERE i.productId = :productId")
    int addStock(String productId, int amount);
}