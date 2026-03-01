package com.ecommerce.shipping.repository;

import com.ecommerce.shipping.entity.TrackingHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TrackingHistoryRepository extends JpaRepository<TrackingHistory, UUID> {
    List<TrackingHistory> findByShipmentIdOrderByCreatedAtDesc(UUID shipmentId);
}

