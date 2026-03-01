package com.ecommerce.shipping.controller;

import com.ecommerce.shipping.dto.request.UpdateTrackingRequest;
import com.ecommerce.shipping.dto.response.ApiResponse;
import com.ecommerce.shipping.service.ShippingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {
    private final ShippingService shippingService;

    public WebhookController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    @PostMapping("/ghn")
    public ResponseEntity<ApiResponse<Void>> handleGHNWebhook(
            @RequestBody UpdateTrackingRequest request) {
        log.info("GHN webhook received for tracking: {}", request.getTrackingNumber());
        try {
            shippingService.updateTracking(request.getTrackingNumber());
            return ResponseEntity.ok(ApiResponse.success(null, "GHN webhook processed successfully"));
        } catch (Exception e) {
            log.error("Error processing GHN webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Error processing webhook"));
        }
    }

    @PostMapping("/ghtk")
    public ResponseEntity<ApiResponse<Void>> handleGHTKWebhook(
            @RequestBody UpdateTrackingRequest request) {
        log.info("GHTK webhook received for tracking: {}", request.getTrackingNumber());
        try {
            shippingService.updateTracking(request.getTrackingNumber());
            return ResponseEntity.ok(ApiResponse.success(null, "GHTK webhook processed successfully"));
        } catch (Exception e) {
            log.error("Error processing GHTK webhook", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Error processing webhook"));
        }
    }
}

