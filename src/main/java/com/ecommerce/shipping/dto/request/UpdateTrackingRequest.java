package com.ecommerce.shipping.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateTrackingRequest implements Serializable {
    @NotBlank(message = "Tracking number cannot be blank")
    private String trackingNumber;

    private String status;
    private String location;
    private String description;
}

