package com.ecommerce.shipping.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculateFeeResponse {
    private String provider;
    private BigDecimal fee;
    private Integer estimatedDays;
    private String serviceType;
    @Builder.Default
    private String currency = "VND";
}

