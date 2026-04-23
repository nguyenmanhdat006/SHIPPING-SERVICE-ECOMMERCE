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
    private String toName;

    @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^(0|\\+84)[0-9]{9,10}$", message = "Invalid phone number")
    private String toPhone;

    @NotBlank(message = "Address is required")
    private String toAddress;

    @NotNull(message = "District ID is required")
    private Integer toDistrictId;

    @NotBlank(message = "Ward code is required")
    private String toWardCode;

    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be at least 1 gram")
    private Integer weight;

    private Integer length;
    private Integer width;
    private Integer height;

    @NotNull(message = "COD amount is required")
    @DecimalMin(value = "0", message = "COD amount must be at least 0")
    private BigDecimal codAmount;

    private String note;
}

