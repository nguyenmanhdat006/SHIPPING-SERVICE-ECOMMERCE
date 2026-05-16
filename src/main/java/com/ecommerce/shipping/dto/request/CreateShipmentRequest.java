package com.ecommerce.shipping.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentRequest {
    @NotBlank(message = "Order ID is required")
    private String orderId;

    @NotBlank(message = "Order number is required")
    private String orderNumber;

    @NotBlank(message = "Recipient name is required")
    private String recipientName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Invalid phone number")
    private String phone;

    @NotBlank(message = "Address is required")
    private String address;

    @NotNull(message = "Shipping fee is required")
    @DecimalMin(value = "0", message = "Shipping fee must be at least 0")
    private BigDecimal shippingFee;

    @NotNull(message = "COD amount is required")
    @DecimalMin(value = "0", message = "COD amount must be at least 0")
    private BigDecimal codAmount;

    @NotNull(message = "Estimated days is required")
    @Min(value = 1, message = "Estimated days must be at least 1")
    private Integer estimatedDays;

    private String note;
}

