package com.ecommerce.shipping.controller;

import com.ecommerce.shipping.dto.request.CalculateFeeRequest;
import com.ecommerce.shipping.dto.request.CreateShipmentRequest;
import com.ecommerce.shipping.dto.response.CalculateFeeResponse;
import com.ecommerce.shipping.dto.response.CreateShipmentResponse;
import com.ecommerce.shipping.dto.response.TrackingResponse;
import com.ecommerce.shipping.service.ShippingService;
import jakarta.validation.Valid;
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
        log.info("Calculating shipping fee for district: {}", request.getToDistrictId());
        return ResponseEntity.ok(shippingService.calculateFee(request));
    }

    @PostMapping("/create")
    public ResponseEntity<CreateShipmentResponse> createShipment(
            @Valid @RequestBody CreateShipmentRequest request) {
        log.info("Creating shipment for order: {}", request.getOrderNumber());
        return ResponseEntity.ok(shippingService.createShipment(request));
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<CreateShipmentResponse> getShipmentByOrder(
            @PathVariable String orderId) {
        log.info("Getting shipment for order: {}", orderId);
        return ResponseEntity.ok(shippingService.getShipmentByOrderId(orderId));
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<TrackingResponse> getShipmentByTracking(
            @PathVariable String trackingNumber) {
        log.info("Tracking shipment: {}", trackingNumber);
        return ResponseEntity.ok(shippingService.trackShipment(trackingNumber));
    }
}

