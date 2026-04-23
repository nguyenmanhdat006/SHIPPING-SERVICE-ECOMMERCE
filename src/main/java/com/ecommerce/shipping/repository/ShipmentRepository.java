package com.ecommerce.shipping.repository;

import com.ecommerce.shipping.entity.Shipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    Optional<Shipment> findByTrackingNumber(String trackingNumber);
    Optional<Shipment> findByOrderId(String orderId);
    Optional<Shipment> findByShipmentNumber(String shipmentNumber);
}

