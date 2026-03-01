package com.ecommerce.shipping.dto.request;

import com.ecommerce.shipping.enums.ShippingProvider;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShipmentRequest implements Serializable {
    @NotBlank(message = "Order ID cannot be blank")
    private String orderId;

    @NotBlank(message = "From address cannot be blank")
    private String fromAddress;

    @NotBlank(message = "To address cannot be blank")
    private String toAddress;

    @NotBlank(message = "Recipient name cannot be blank")
    private String recipientName;

    @NotBlank(message = "Recipient phone cannot be blank")
    private String recipientPhone;

    @NotNull(message = "Weight cannot be null")
    private Integer weight;

    @NotNull(message = "Provider cannot be null")
    private ShippingProvider provider;

    private String notes;

    private String fromDistrictId;

    private String toDistrictId;
}

