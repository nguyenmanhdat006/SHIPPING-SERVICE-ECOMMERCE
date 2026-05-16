package com.ecommerce.shipping.entity;

import com.ecommerce.shipping.enums.ShipmentStatus;
import com.ecommerce.shipping.enums.ShippingProvider;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "shipments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Shipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String shipmentNumber;

    @Column(nullable = false)
    private String orderId;

    @Column(nullable = false)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ShippingProvider provider;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ShipmentStatus status;

    @Column(length = 100)
    private String trackingNumber;

    @Column(precision = 19, scale = 2)
    private BigDecimal shippingFee;

    @Column(precision = 19, scale = 2)
    private BigDecimal codAmount;

    @Column(length = 50)
    private String serviceType;

    @Column(nullable = false, length = 100)
    private String toName;

    @Column(nullable = false, length = 20)
    private String toPhone;

    @Column(nullable = false, length = 500)
    private String toAddress;

    @Column(nullable = false)
    private Integer toDistrictId;

    @Column(nullable = false, length = 20)
    private String toWardCode;

    @Column(nullable = false)
    private Integer weight;

    private Integer length;
    private Integer width;
    private Integer height;

    @Column(length = 200)
    private String currentLocation;

    @Column(columnDefinition = "text")
    private String signature;

    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime expectedDeliveryAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ShipmentStatus.PENDING;
        }
        if (provider == null) {
            provider = ShippingProvider.GHN;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

