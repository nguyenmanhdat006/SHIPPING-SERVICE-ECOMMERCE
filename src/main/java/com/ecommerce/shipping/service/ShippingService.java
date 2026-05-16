package com.ecommerce.shipping.service;

import com.ecommerce.shipping.dto.request.CalculateFeeRequest;
import com.ecommerce.shipping.dto.request.CreateShipmentRequest;
import com.ecommerce.shipping.dto.response.CalculateFeeResponse;
import com.ecommerce.shipping.dto.response.CreateShipmentResponse;
import com.ecommerce.shipping.dto.response.ShipmentResponse;
import com.ecommerce.shipping.enums.ShipmentStatus;

public interface ShippingService {

    CalculateFeeResponse calculateFee(CalculateFeeRequest request);

    CreateShipmentResponse createShipment(CreateShipmentRequest request);

    ShipmentResponse getShipmentById(Long id);

    CreateShipmentResponse getShipmentByOrderId(String orderId);

    ShipmentResponse updateShipmentStatus(Long id, ShipmentStatus status);

    ShipmentResponse markDelivered(Long id, java.time.LocalDateTime deliveredAt, String signature);
}

