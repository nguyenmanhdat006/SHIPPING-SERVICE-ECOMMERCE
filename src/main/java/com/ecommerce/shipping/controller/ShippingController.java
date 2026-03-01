package com.ecommerce.shipping.controller;

import com.ecommerce.shipping.dto.request.CalculateShippingFeeRequest;
import com.ecommerce.shipping.dto.request.CreateShipmentRequest;
import com.ecommerce.shipping.dto.response.ApiResponse;
import com.ecommerce.shipping.dto.response.ShipmentResponse;
import com.ecommerce.shipping.dto.response.ShippingFeeResponse;
import com.ecommerce.shipping.entity.TrackingHistory;
import com.ecommerce.shipping.service.ShippingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/shipping")
public class ShippingController {
    private final ShippingService shippingService;

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/calculate-fee")
    public ResponseEntity<ApiResponse<ShippingFeeResponse>> calculateShippingFee(
            @Valid @RequestBody CalculateShippingFeeRequest request) {
        log.info("Calculate shipping fee request received");
        try {
            ShippingFeeResponse response = shippingService.calculateShippingFee(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Shipping fee calculated successfully"));
        } catch (Exception e) {
            log.error("Error calculating shipping fee", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<ShipmentResponse>> createShipment(
            @Valid @RequestBody CreateShipmentRequest request) {
        log.info("Create shipment request received for order: {}", request.getOrderId());
        try {
            ShipmentResponse response = shippingService.createShipment(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(response, "Shipment created successfully"));
        } catch (Exception e) {
            log.error("Error creating shipment", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByOrder(
            @PathVariable String orderId) {
        log.info("Get shipment by order request for order: {}", orderId);
        try {
            ShipmentResponse response = shippingService.getShipmentByOrderId(orderId);
            return ResponseEntity.ok(ApiResponse.success(response, "Shipment retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting shipment", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<ApiResponse<ShipmentResponse>> getShipmentByTracking(
            @PathVariable String trackingNumber) {
        log.info("Get shipment by tracking number: {}", trackingNumber);
        try {
            ShipmentResponse response = shippingService.getShipmentByTrackingNumber(trackingNumber);
            return ResponseEntity.ok(ApiResponse.success(response, "Shipment retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting shipment by tracking number", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        }
    }

    @PostMapping("/track/{trackingNumber}/update")
    public ResponseEntity<ApiResponse<ShipmentResponse>> updateTracking(
            @PathVariable String trackingNumber) {
        log.info("Update tracking request for: {}", trackingNumber);
        try {
            ShipmentResponse response = shippingService.updateTracking(trackingNumber);
            return ResponseEntity.ok(ApiResponse.success(response, "Tracking updated successfully"));
        } catch (Exception e) {
            log.error("Error updating tracking", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @DeleteMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelShipment(@PathVariable UUID id) {
        log.info("Cancel shipment request for: {}", id);
        try {
            shippingService.cancelShipment(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Shipment cancelled successfully"));
        } catch (Exception e) {
            log.error("Error cancelling shipment", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }

    @GetMapping("/{id}/tracking-history")
    public ResponseEntity<ApiResponse<List<TrackingHistory>>> getTrackingHistory(
            @PathVariable UUID id) {
        log.info("Get tracking history for shipment: {}", id);
        try {
            List<TrackingHistory> history = shippingService.getTrackingHistory(id);
            return ResponseEntity.ok(ApiResponse.success(history, "Tracking history retrieved successfully"));
        } catch (Exception e) {
            log.error("Error getting tracking history", e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, e.getMessage()));
        }
    }
}

