package com.ecommerce.shipping.service;

import com.ecommerce.shipping.dto.request.CalculateFeeRequest;
import com.ecommerce.shipping.dto.request.CreateShipmentRequest;
import com.ecommerce.shipping.dto.response.CalculateFeeResponse;
import com.ecommerce.shipping.dto.response.CreateShipmentResponse;
import com.ecommerce.shipping.dto.response.TrackingResponse;

public interface ShippingService {

    CalculateFeeResponse calculateFee(CalculateFeeRequest request);

    CreateShipmentResponse createShipment(CreateShipmentRequest request);

    TrackingResponse trackShipment(String trackingNumber);

    CreateShipmentResponse getShipmentByOrderId(String orderId);
}

