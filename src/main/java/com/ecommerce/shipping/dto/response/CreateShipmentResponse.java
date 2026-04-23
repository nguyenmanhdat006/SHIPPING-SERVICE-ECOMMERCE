package com.ecommerce.shipping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentResponse {
    private Long shipmentId;
    private String shipmentNumber;
    private String trackingNumber;
    private String provider;
    private BigDecimal shippingFee;
    private LocalDateTime estimatedDelivery;
}

