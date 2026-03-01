package com.ecommerce.shipping.service;

import com.ecommerce.shipping.dto.request.CalculateShippingFeeRequest;
import com.ecommerce.shipping.dto.request.CreateShipmentRequest;
import com.ecommerce.shipping.dto.response.ShipmentResponse;
import com.ecommerce.shipping.dto.response.ShippingFeeResponse;
import com.ecommerce.shipping.entity.Shipment;
import com.ecommerce.shipping.entity.TrackingHistory;
import com.ecommerce.shipping.enums.ShipmentStatus;
import com.ecommerce.shipping.enums.ShippingProvider;
import com.ecommerce.shipping.repository.ShipmentRepository;
import com.ecommerce.shipping.repository.TrackingHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class ShippingService {
    private final ShipmentRepository shipmentRepository;
    private final TrackingHistoryRepository trackingHistoryRepository;
    private final GHNService ghnService;
    private final GHTKService ghtkService;

    public ShippingService(ShipmentRepository shipmentRepository,
                           TrackingHistoryRepository trackingHistoryRepository,
                           GHNService ghnService,
                           GHTKService ghtkService) {
        this.shipmentRepository = shipmentRepository;
        this.trackingHistoryRepository = trackingHistoryRepository;
        this.ghnService = ghnService;
        this.ghtkService = ghtkService;
    }

    public ShippingFeeResponse calculateShippingFee(CalculateShippingFeeRequest request) {
        log.info("Calculating shipping fee from {} to {}", request.getFromAddress(), request.getToAddress());

        try {
            if (request.getWeight() > 0) {
                return ghnService.calculateShippingFee(
                        request.getFromDistrictId(),
                        request.getToDistrictId(),
                        request.getWeight()
                );
            }
            throw new IllegalArgumentException("Weight must be greater than 0");
        } catch (Exception e) {
            log.error("Error calculating shipping fee", e);
            throw new RuntimeException("Failed to calculate shipping fee", e);
        }
    }

    public ShipmentResponse createShipment(CreateShipmentRequest request) {
        log.info("Creating shipment for order: {}", request.getOrderId());

        try {
            // Check if shipment already exists for this order
            if (shipmentRepository.findByOrderId(request.getOrderId()).isPresent()) {
                throw new RuntimeException("Shipment already exists for order: " + request.getOrderId());
            }

            // Calculate shipping fee
            ShippingFeeResponse feeResponse = ghnService.calculateShippingFee(
                    request.getFromDistrictId(),
                    request.getToDistrictId(),
                    request.getWeight()
            );

            // Create order with provider
            Map<String, Object> orderData = new HashMap<>();
            orderData.put("to_name", request.getRecipientName());
            orderData.put("to_phone", request.getRecipientPhone());
            orderData.put("to_address", request.getToAddress());
            orderData.put("to_district_id", request.getToDistrictId());
            orderData.put("weight", request.getWeight());
            orderData.put("service_id", 2);

            String trackingNumber;
            if (request.getProvider() == ShippingProvider.GHN) {
                trackingNumber = ghnService.createOrder(orderData);
            } else if (request.getProvider() == ShippingProvider.GHTK) {
                trackingNumber = ghtkService.createOrder(orderData);
            } else {
                throw new RuntimeException("Unsupported shipping provider: " + request.getProvider());
            }

            // Create shipment entity
            Shipment shipment = Shipment.builder()
                    .orderId(request.getOrderId())
                    .trackingNumber(trackingNumber)
                    .provider(request.getProvider())
                    .status(ShipmentStatus.CONFIRMED)
                    .shippingFee(feeResponse.getFee())
                    .fromAddress(request.getFromAddress())
                    .toAddress(request.getToAddress())
                    .recipientName(request.getRecipientName())
                    .recipientPhone(request.getRecipientPhone())
                    .weight(request.getWeight())
                    .notes(request.getNotes())
                    .build();

            Shipment savedShipment = shipmentRepository.save(shipment);

            // Add initial tracking history
            TrackingHistory tracking = TrackingHistory.builder()
                    .shipment(savedShipment)
                    .status(ShipmentStatus.CONFIRMED)
                    .description("Shipment confirmed with tracking number: " + trackingNumber)
                    .build();
            trackingHistoryRepository.save(tracking);

            log.info("Shipment created successfully with tracking number: {}", trackingNumber);
            return mapToResponse(savedShipment);

        } catch (Exception e) {
            log.error("Error creating shipment", e);
            throw new RuntimeException("Failed to create shipment", e);
        }
    }

    public ShipmentResponse getShipmentByOrderId(String orderId) {
        log.info("Getting shipment for order: {}", orderId);

        return shipmentRepository.findByOrderId(orderId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Shipment not found for order: " + orderId));
    }

    public ShipmentResponse getShipmentByTrackingNumber(String trackingNumber) {
        log.info("Getting shipment by tracking number: {}", trackingNumber);

        return shipmentRepository.findByTrackingNumber(trackingNumber)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Shipment not found with tracking number: " + trackingNumber));
    }

    public ShipmentResponse updateTracking(String trackingNumber) {
        log.info("Updating tracking for: {}", trackingNumber);

        try {
            Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                    .orElseThrow(() -> new RuntimeException("Shipment not found: " + trackingNumber));

            // Fetch latest status from provider
            Map<String, Object> trackingInfo;
            if (shipment.getProvider() == ShippingProvider.GHN) {
                trackingInfo = ghnService.getTrackingInfo(trackingNumber);
            } else if (shipment.getProvider() == ShippingProvider.GHTK) {
                trackingInfo = ghtkService.getTrackingInfo(trackingNumber);
            } else {
                throw new RuntimeException("Unsupported provider: " + shipment.getProvider());
            }

            // Update shipment status based on tracking info
            String newStatus = (String) trackingInfo.get("status");
            if (newStatus != null && !newStatus.equals(shipment.getStatus().toString())) {
                shipment.setStatus(ShipmentStatus.valueOf(newStatus.toUpperCase()));

                // Add tracking history
                TrackingHistory tracking = TrackingHistory.builder()
                        .shipment(shipment)
                        .status(shipment.getStatus())
                        .location((String) trackingInfo.get("location"))
                        .description((String) trackingInfo.get("description"))
                        .build();
                trackingHistoryRepository.save(tracking);
            }

            shipmentRepository.save(shipment);
            return mapToResponse(shipment);

        } catch (Exception e) {
            log.error("Error updating tracking", e);
            throw new RuntimeException("Failed to update tracking", e);
        }
    }

    public void cancelShipment(UUID shipmentId) {
        log.info("Cancelling shipment: {}", shipmentId);

        try {
            Shipment shipment = shipmentRepository.findById(shipmentId)
                    .orElseThrow(() -> new RuntimeException("Shipment not found: " + shipmentId));

            if (shipment.getStatus() == ShipmentStatus.DELIVERED) {
                throw new RuntimeException("Cannot cancel delivered shipment");
            }

            shipment.setStatus(ShipmentStatus.CANCELLED);

            TrackingHistory tracking = TrackingHistory.builder()
                    .shipment(shipment)
                    .status(ShipmentStatus.CANCELLED)
                    .description("Shipment cancelled")
                    .build();
            trackingHistoryRepository.save(tracking);

            shipmentRepository.save(shipment);
            log.info("Shipment cancelled successfully: {}", shipmentId);

        } catch (Exception e) {
            log.error("Error cancelling shipment", e);
            throw new RuntimeException("Failed to cancel shipment", e);
        }
    }

    public List<TrackingHistory> getTrackingHistory(UUID shipmentId) {
        log.info("Getting tracking history for shipment: {}", shipmentId);
        return trackingHistoryRepository.findByShipmentIdOrderByCreatedAtDesc(shipmentId);
    }

    private ShipmentResponse mapToResponse(Shipment shipment) {
        return ShipmentResponse.builder()
                .id(shipment.getId().toString())
                .orderId(shipment.getOrderId())
                .trackingNumber(shipment.getTrackingNumber())
                .provider(shipment.getProvider().name())
                .status(shipment.getStatus().name())
                .shippingFee(shipment.getShippingFee())
                .fromAddress(shipment.getFromAddress())
                .toAddress(shipment.getToAddress())
                .recipientName(shipment.getRecipientName())
                .recipientPhone(shipment.getRecipientPhone())
                .weight(shipment.getWeight())
                .notes(shipment.getNotes())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }
}

