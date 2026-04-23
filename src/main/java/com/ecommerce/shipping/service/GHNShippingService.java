package com.ecommerce.shipping.service;

import com.ecommerce.shipping.client.GHNClient;
import com.ecommerce.shipping.dto.request.CalculateFeeRequest;
import com.ecommerce.shipping.dto.request.CreateShipmentRequest;
import com.ecommerce.shipping.dto.response.CalculateFeeResponse;
import com.ecommerce.shipping.dto.response.CreateShipmentResponse;
import com.ecommerce.shipping.dto.response.TrackingResponse;
import com.ecommerce.shipping.entity.Shipment;
import com.ecommerce.shipping.enums.ShipmentStatus;
import com.ecommerce.shipping.enums.ShippingProvider;
import com.ecommerce.shipping.exception.ShipmentNotFoundException;
import com.ecommerce.shipping.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GHNShippingService implements ShippingService {

    private final GHNClient ghnClient;
    private final ShipmentRepository shipmentRepository;

    @Override
    public CalculateFeeResponse calculateFee(CalculateFeeRequest request) {
        Map<String, Object> response = ghnClient.calculateFee(
                request.getToDistrictId(),
                request.getToWardCode(),
                request.getWeight(),
                request.getServiceTypeId() != null ? request.getServiceTypeId() : 2
        );

        Map<String, Object> data = getDataMap(response);
        BigDecimal fee = toBigDecimal(data.get("total"));

        return CalculateFeeResponse.builder()
                .provider("GHN")
                .fee(fee)
                .estimatedDays(3)
                .serviceType("Standard")
                .build();
    }

    @Override
    public CreateShipmentResponse createShipment(CreateShipmentRequest request) {
        String shipmentNumber = generateShipmentNumber();

        Map<String, Object> feeResponse = ghnClient.calculateFee(
                request.getToDistrictId(),
                request.getToWardCode(),
                request.getWeight(),
                2
        );
        BigDecimal shippingFee = toBigDecimal(getDataMap(feeResponse).get("total"));

        Map<String, Object> createResponse = ghnClient.createOrder(
                request.getOrderNumber(),
                request.getToName(),
                request.getToPhone(),
                request.getToAddress(),
                request.getToDistrictId(),
                request.getToWardCode(),
                request.getWeight(),
                request.getCodAmount().intValue(),
                request.getNote()
        );

        String trackingNumber = String.valueOf(getDataMap(createResponse).get("order_code"));
        LocalDateTime estimatedDelivery = LocalDateTime.now().plusDays(3);

        Shipment shipment = Shipment.builder()
                .shipmentNumber(shipmentNumber)
                .orderId(request.getOrderId())
                .orderNumber(request.getOrderNumber())
                .provider(ShippingProvider.GHN)
                .status(ShipmentStatus.PENDING)
                .trackingNumber(trackingNumber)
                .shippingFee(shippingFee)
                .codAmount(request.getCodAmount())
                .serviceType("Standard")
                .toName(request.getToName())
                .toPhone(request.getToPhone())
                .toAddress(request.getToAddress())
                .toDistrictId(request.getToDistrictId())
                .toWardCode(request.getToWardCode())
                .weight(request.getWeight())
                .length(request.getLength())
                .width(request.getWidth())
                .height(request.getHeight())
                .expectedDeliveryAt(estimatedDelivery)
                .build();

        Shipment savedShipment = shipmentRepository.save(shipment);
        return mapToCreateResponse(savedShipment);
    }

    @Override
    public TrackingResponse trackShipment(String trackingNumber) {
        Shipment shipment = shipmentRepository.findByTrackingNumber(trackingNumber)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found for tracking number: " + trackingNumber));

        Map<String, Object> trackingResponse = ghnClient.getTracking(trackingNumber);
        Map<String, Object> data = getDataMap(trackingResponse);

        String ghnStatus = toStringOrNull(data.get("status"));
        ShipmentStatus mappedStatus = mapGHNStatus(ghnStatus);
        shipment.setStatus(mappedStatus);
        shipment.setCurrentLocation(toStringOrNull(data.get("current_warehouse_name")));

        if (mappedStatus == ShipmentStatus.PICKED_UP && shipment.getPickedUpAt() == null) {
            shipment.setPickedUpAt(LocalDateTime.now());
        }
        if (mappedStatus == ShipmentStatus.DELIVERED && shipment.getDeliveredAt() == null) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }

        shipmentRepository.save(shipment);

        return TrackingResponse.builder()
                .trackingNumber(shipment.getTrackingNumber())
                .status(shipment.getStatus().name())
                .currentLocation(shipment.getCurrentLocation())
                .expectedDelivery(shipment.getExpectedDeliveryAt())
                .events(extractEvents(data))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CreateShipmentResponse getShipmentByOrderId(String orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found for order ID: " + orderId));
        return mapToCreateResponse(shipment);
    }

    private CreateShipmentResponse mapToCreateResponse(Shipment shipment) {
        return CreateShipmentResponse.builder()
                .shipmentId(shipment.getId())
                .shipmentNumber(shipment.getShipmentNumber())
                .trackingNumber(shipment.getTrackingNumber())
                .provider(shipment.getProvider().name())
                .shippingFee(shipment.getShippingFee())
                .estimatedDelivery(shipment.getExpectedDeliveryAt())
                .build();
    }

    private String generateShipmentNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        int randomPart = new Random().nextInt(9000) + 1000;
        return "SHIP-" + datePart + "-" + randomPart;
    }

    private ShipmentStatus mapGHNStatus(String ghnStatus) {
        if (ghnStatus == null) {
            return ShipmentStatus.PENDING;
        }

        return switch (ghnStatus.toLowerCase()) {
            case "ready_to_pick" -> ShipmentStatus.PENDING;
            case "picking" -> ShipmentStatus.PICKED_UP;
            case "transporting" -> ShipmentStatus.IN_TRANSIT;
            case "delivering" -> ShipmentStatus.OUT_FOR_DELIVERY;
            case "delivered" -> ShipmentStatus.DELIVERED;
            case "delivery_fail", "failed" -> ShipmentStatus.FAILED_DELIVERY;
            case "cancel", "cancelled" -> ShipmentStatus.CANCELLED;
            default -> ShipmentStatus.IN_TRANSIT;
        };
    }

    private List<TrackingResponse.TrackingEvent> extractEvents(Map<String, Object> data) {
        Object rawLogs = data.get("log");
        if (!(rawLogs instanceof List<?> logs)) {
            return List.of();
        }

        List<TrackingResponse.TrackingEvent> events = new ArrayList<>();
        for (Object logItem : logs) {
            if (!(logItem instanceof Map<?, ?> eventMap)) {
                continue;
            }

            String status = toStringOrNull(eventMap.get("status"));
            String location = toStringOrNull(eventMap.get("location"));
            String description = toStringOrNull(eventMap.get("description"));
            LocalDateTime timestamp = parseDateTime(eventMap.get("updated_date"));

            events.add(TrackingResponse.TrackingEvent.builder()
                    .status(mapGHNStatus(status).name())
                    .location(location)
                    .description(description)
                    .timestamp(timestamp)
                    .build());
        }
        return events;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> getDataMap(Map<String, Object> response) {
        if (response == null) {
            throw new RuntimeException("Empty response from GHN API");
        }

        Object data = response.get("data");
        if (data instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }

        throw new RuntimeException("Invalid GHN response format");
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(String.valueOf(value));
        } catch (NumberFormatException ex) {
            log.warn("Cannot parse decimal value: {}", value);
            return BigDecimal.ZERO;
        }
    }

    private String toStringOrNull(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private LocalDateTime parseDateTime(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(String.valueOf(value));
        } catch (Exception ex) {
            return null;
        }
    }
}

