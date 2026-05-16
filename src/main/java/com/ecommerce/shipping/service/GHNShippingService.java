package com.ecommerce.shipping.service;

import com.ecommerce.shipping.dto.request.CalculateFeeRequest;
import com.ecommerce.shipping.dto.request.CreateShipmentRequest;
import com.ecommerce.shipping.dto.response.CalculateFeeResponse;
import com.ecommerce.shipping.dto.response.CreateShipmentResponse;
import com.ecommerce.shipping.dto.response.ShipmentResponse;
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
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class GHNShippingService implements ShippingService {

    private final ShipmentRepository shipmentRepository;

    @Override
    public CalculateFeeResponse calculateFee(CalculateFeeRequest request) {
        BigDecimal fee = calculateManualFee(request.getCity(), request.getWeight());
        return CalculateFeeResponse.builder()
                .shippingFee(fee)
                .estimatedDays(3)
                .build();
    }

    @Override
    public CreateShipmentResponse createShipment(CreateShipmentRequest request) {
        Shipment shipment = Shipment.builder()
                .shipmentNumber(generateShipmentNumber())
                .orderId(request.getOrderId())
                .orderNumber(request.getOrderNumber())
                .provider(ShippingProvider.GHN)
                .status(ShipmentStatus.PENDING)
                .trackingNumber(generateShipmentNumber())
                .shippingFee(request.getShippingFee())
                .codAmount(request.getCodAmount())
                .serviceType("Standard")
                .toName(request.getRecipientName())
                .toPhone(request.getPhone())
                .toAddress(request.getAddress())
                .toDistrictId(0)
                .toWardCode("MANUAL")
                .weight(0)
                .expectedDeliveryAt(LocalDateTime.now().plusDays(request.getEstimatedDays()))
                .currentLocation("Warehouse")
                .build();

        Shipment savedShipment = shipmentRepository.save(shipment);
        return mapToCreateResponse(savedShipment);
    }

    @Override
    @Transactional(readOnly = true)
    public ShipmentResponse getShipmentById(Long id) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + id));
        return mapToShipmentResponse(shipment);
    }

    @Override
    @Transactional(readOnly = true)
    public CreateShipmentResponse getShipmentByOrderId(String orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found for order ID: " + orderId));
        return mapToCreateResponse(shipment);
    }

    @Override
    public ShipmentResponse updateShipmentStatus(Long id, ShipmentStatus status) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + id));

        shipment.setStatus(status);
        if (status == ShipmentStatus.PICKED_UP && shipment.getPickedUpAt() == null) {
            shipment.setPickedUpAt(LocalDateTime.now());
        }
        if (status == ShipmentStatus.DELIVERED && shipment.getDeliveredAt() == null) {
            shipment.setDeliveredAt(LocalDateTime.now());
        }

        Shipment savedShipment = shipmentRepository.save(shipment);
        return mapToShipmentResponse(savedShipment);
    }

    @Override
    public ShipmentResponse markDelivered(Long id, LocalDateTime deliveredAt, String signature) {
        Shipment shipment = shipmentRepository.findById(id)
                .orElseThrow(() -> new ShipmentNotFoundException("Shipment not found with id: " + id));

        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(deliveredAt != null ? deliveredAt : LocalDateTime.now());
        shipment.setSignature(signature);

        Shipment savedShipment = shipmentRepository.save(shipment);
        return mapToShipmentResponse(savedShipment);
    }

    private CalculateFeeResponse mapToCalculateFeeResponse(BigDecimal fee) {
        return CalculateFeeResponse.builder()
                .shippingFee(fee)
                .estimatedDays(3)
                .build();
    }

    private CreateShipmentResponse mapToCreateResponse(Shipment shipment) {
        return CreateShipmentResponse.builder()
                .shipmentId(shipment.getId())
                .shipmentNumber(shipment.getShipmentNumber())
                .orderId(shipment.getOrderId())
                .orderNumber(shipment.getOrderNumber())
                .status(shipment.getStatus().name())
                .shippingFee(shipment.getShippingFee())
                .codAmount(shipment.getCodAmount())
                .estimatedDelivery(shipment.getExpectedDeliveryAt())
                .createdAt(shipment.getCreatedAt())
                .build();
    }

    private ShipmentResponse mapToShipmentResponse(Shipment shipment) {
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrderId())
                .orderNumber(shipment.getOrderNumber())
                .status(shipment.getStatus().name())
                .shippingFee(shipment.getShippingFee())
                .codAmount(shipment.getCodAmount())
                .recipientName(shipment.getToName())
                .recipientPhone(shipment.getToPhone())
                .address(shipment.getToAddress())
                .estimatedDelivery(shipment.getExpectedDeliveryAt())
                .createdAt(shipment.getCreatedAt())
                .updatedAt(shipment.getUpdatedAt())
                .build();
    }

    private BigDecimal calculateManualFee(String city, Integer weight) {
        BigDecimal fee = BigDecimal.valueOf(20000);
        if (city != null && !"Ha Noi".equalsIgnoreCase(city.trim())) {
            fee = fee.add(BigDecimal.valueOf(10000));
        }
        if (weight != null && weight > 1000) {
            long extraUnits = Math.max(0, (weight - 1000L) / 500L);
            fee = fee.add(BigDecimal.valueOf(extraUnits * 5000L));
        }
        return fee;
    }

    private String generateShipmentNumber() {
        String datePart = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        int randomPart = new Random().nextInt(9000) + 1000;
        return "SHIP-" + datePart + "-" + randomPart;
    }
}

