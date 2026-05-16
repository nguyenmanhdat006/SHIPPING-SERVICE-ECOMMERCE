package com.ecommerce.shipping.controller;

import com.ecommerce.shipping.dto.request.CalculateFeeRequest;
import com.ecommerce.shipping.dto.request.CreateShipmentRequest;
import com.ecommerce.shipping.dto.response.CalculateFeeResponse;
import com.ecommerce.shipping.dto.response.CreateShipmentResponse;
import com.ecommerce.shipping.dto.response.ShipmentResponse;
import com.ecommerce.shipping.enums.ShipmentStatus;
import com.ecommerce.shipping.service.ShippingService;
import jakarta.validation.Valid;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/shipping")
public class ShippingController {
    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/calculate-fee")
    public ResponseEntity<CalculateFeeResponse> calculateShippingFee(
            @Valid @RequestBody CalculateFeeRequest request) {
        log.info("Calculating shipping fee for city: {}, province: {}", request.getCity(), request.getProvince());
        return ResponseEntity.ok(shippingService.calculateFee(request));
    }

    @PostMapping("/create")
    public ResponseEntity<CreateShipmentResponse> createShipment(
            @Valid @RequestBody CreateShipmentRequest request) {
        log.info("Creating shipment for order: {}", request.getOrderNumber());
        return ResponseEntity.ok(shippingService.createShipment(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShipmentResponse> getShipmentById(@PathVariable Long id) {
        log.info("Getting shipment by id: {}", id);
        return ResponseEntity.ok(shippingService.getShipmentById(id));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<ShipmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody ShipmentStatusRequest request) {
        log.info("Updating shipment status for {} to {}", id, request.getStatus());
        return ResponseEntity.ok(shippingService.updateShipmentStatus(id, request.getStatus()));
    }

    @PutMapping("/{id}/deliver")
    public ResponseEntity<ShipmentResponse> markDelivered(
            @PathVariable Long id,
            @RequestBody DeliverShipmentRequest request) {
        log.info("Marking shipment delivered: {}", id);
        return ResponseEntity.ok(shippingService.markDelivered(id, request.getDeliveredAt(), request.getSignature()));
    }

    @Data
    public static class ShipmentStatusRequest {
        private ShipmentStatus status;
    }

    @Data
    public static class DeliverShipmentRequest {
        private java.time.LocalDateTime deliveredAt;
        private String signature;
    }
}

