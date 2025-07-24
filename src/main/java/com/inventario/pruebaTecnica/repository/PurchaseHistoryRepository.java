package com.inventario.pruebaTecnica.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.inventario.pruebaTecnica.entities.PurchaseHistory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PurchaseHistoryRepository extends JpaRepository<PurchaseHistory, Long> {

    /**
     * Retrieves purchase history for a product sorted by date (newest first)
     * @param productId Product ID to search for
     * @return List of purchase records
     */
    List<PurchaseHistory> findByProductIdOrderByPurchaseDateDesc(UUID productId);

    /**
     * Finds purchases within a date range
     * @param startDate Start date (inclusive)
     * @param endDate End date (inclusive)
     * @return List of purchases in the period
     */
    @Query("SELECT ph FROM PurchaseHistory ph WHERE ph.purchaseDate BETWEEN :startDate AND :endDate")
    List<PurchaseHistory> findPurchasesBetweenDates(LocalDateTime startDate, LocalDateTime endDate);
}