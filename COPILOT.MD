# SHIPPING SERVICE - GUIDE

**Port:** 8088 | **Files:** 22 | **Time:** 2h

---

## 📁 STRUCTURE

```
src/main/java/com/ecommerce/shippingservice/
├── ShippingServiceApplication.java
├── config/
│   ├── SecurityConfig.java
│   └── WebClientConfig.java
├── entity/
│   ├── Shipment.java
│   └── TrackingHistory.java
├── repository/
│   ├── ShipmentRepository.java
│   └── TrackingHistoryRepository.java
├── enums/
│   ├── ShipmentStatus.java
│   └── ShippingProvider.java
├── dto/request/
│   ├── CreateShipmentRequest.java
│   ├── CalculateShippingFeeRequest.java
│   └── UpdateTrackingRequest.java
├── dto/response/
│   ├── ApiResponse.java
│   ├── ShipmentResponse.java
│   └── ShippingFeeResponse.java
├── service/
│   ├── ShippingService.java
│   ├── GHNService.java
│   └── GHTKService.java
├── controller/
│   ├── ShippingController.java
│   └── WebhookController.java
└── exception/
    └── GlobalExceptionHandler.java
```

---

## 🎯 KEY FEATURES

### Shipment.java Entity
```
Fields:
- UUID id
- String orderId (FK)
- String trackingNumber
- ShippingProvider provider (GHN, GHTK)
- ShipmentStatus status
- BigDecimal shippingFee
- String fromAddress, toAddress
- String recipientName, phone
- @OneToMany List<TrackingHistory>
- timestamps
```

### ShippingService.java
```
Methods:
- calculateFee(from, to, weight)
  → Call GHN/GHTK API
  → Return estimated fee
  
- createShipment(orderId, address, provider)
  → Create shipment with provider
  → Get tracking number
  → Return shipment details
  
- updateTracking(trackingNumber)
  → Fetch latest status from provider
  → Update shipment status
  → Notify Order Service
  
- getShipmentByOrder(orderId)
- cancelShipment(shipmentId)
```

### GHNService.java (Giao Hàng Nhanh)
```
- calculateShippingFee()
- createOrder()
- getTrackingInfo()
- cancelOrder()
```

---

## 📡 APIs

```
POST /api/shipping/calculate-fee  - Calculate shipping cost
POST /api/shipping/create          - Create shipment
GET  /api/shipping/order/{orderId} - Get by order
GET  /api/shipping/track/{number}  - Track shipment
PUT  /api/shipping/{id}/cancel     - Cancel
POST /api/webhooks/ghn             - GHN webhook
POST /api/webhooks/ghtk            - GHTK webhook
```

---

## 🚚 GHN INTEGRATION

```java
@Service
public class GHNService {
    private final String API_URL = "https://dev-online-gateway.ghn.vn";
    
    public ShippingFeeResponse calculateFee(
        String fromDistrict, String toDistrict, int weight
    ) {
        // Call GHN API
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token", ghnApiKey);
        
        Map<String, Object> body = Map.of(
            "from_district_id", fromDistrict,
            "to_district_id", toDistrict,
            "weight", weight
        );
        
        return webClient.post()
            .uri(API_URL + "/shiip/public-api/v2/shipping-order/fee")
            .headers(h -> h.addAll(headers))
            .bodyValue(body)
            .retrieve()
            .bodyToMono(GHNFeeResponse.class)
            .block();
    }
}
```

---

## 🔗 INTEGRATION

**Order Service → Shipping Service:**
```java
// After order created
ShipmentResponse shipment = shippingService.createShipment(
    orderId, 
    order.getShippingAddress(),
    ShippingProvider.GHN
);

// Get shipping fee
BigDecimal fee = shippingService.calculateFee(
    fromAddress,
    toAddress,
    totalWeight
);
```

---

**Time: 2 hours**