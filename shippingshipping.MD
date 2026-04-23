# SHIPPING SERVICE - GITHUB COPILOT IMPLEMENTATION GUIDE

**GHN (Giao Hàng Nhanh) Integration | Ready for Frontend Integration**

---

## 📊 SERVICE OVERVIEW

**Port:** 8088  
**Database:** shipping_db (PostgreSQL - port 5439)  
**Shipping Provider:** GHN (Giao Hàng Nhanh)  
**GHN Mode:** Sandbox (Test)

---

## 📁 PROJECT STRUCTURE

```
shipping-service/
├── pom.xml
├── src/main/java/com/ecommerce/shippingservice/
│   ├── ShippingServiceApplication.java
│   ├── config/
│   │   ├── GHNConfig.java
│   │   └── WebClientConfig.java
│   ├── controller/
│   │   └── ShippingController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CalculateFeeRequest.java
│   │   │   └── CreateShipmentRequest.java
│   │   └── response/
│   │       ├── CalculateFeeResponse.java
│   │       ├── CreateShipmentResponse.java
│   │       └── TrackingResponse.java
│   ├── entity/
│   │   ├── Shipment.java
│   │   ├── ShippingProvider.java (enum)
│   │   └── ShipmentStatus.java (enum)
│   ├── repository/
│   │   └── ShipmentRepository.java
│   ├── service/
│   │   ├── ShippingService.java
│   │   └── GHNShippingService.java
│   ├── client/
│   │   └── GHNClient.java
│   └── exception/
│       └── ShipmentNotFoundException.java
└── src/main/resources/
    └── application.yml

Total: 16 files
Time: 2-3 hours
```

---

## 🔧 PHASE 1: Dependencies & Configuration (15 min)

### File 1: `pom.xml`

**Copilot Prompt:**
```xml
Add dependencies for Shipping Service with GHN API integration:
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- PostgreSQL Driver
- Spring Boot Starter WebFlux (for WebClient)
- Lombok
- MapStruct
- Eureka Client
- Jackson for JSON parsing
```

**Expected Code:**
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-webflux</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
    </dependency>
    
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>1.5.5.Final</version>
    </dependency>
    
    <dependency>
        <groupId>org.springframework.cloud</groupId>
        <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
    </dependency>
    
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
    </dependency>
</dependencies>
```

---

### File 2: `application.yml`

**Copilot Prompt:**
```yaml
Configure Shipping Service with:
- Port 8088
- PostgreSQL database shipping_db on port 5439
- GHN API configuration (sandbox URL, test token, shop ID, from district)
- Eureka client
```

**Expected Code:**
```yaml
server:
  port: 8088

spring:
  application:
    name: shipping-service
  
  datasource:
    url: jdbc:postgresql://localhost:5439/shipping_db
    username: postgres
    password: postgres
    driver-class-name: org.postgresql.Driver
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true

# GHN API Configuration
ghn:
  api-url: https://dev-online-gateway.ghn.vn/shiip/public-api
  token: YOUR_GHN_TEST_TOKEN_HERE  # Get from https://sship.vn
  shop-id: YOUR_SHOP_ID_HERE       # Your shop ID from GHN
  from-district-id: 1542           # Your warehouse district ID (default: Hoan Kiem, Hanoi)

# Default shipping settings
shipping:
  default-weight: 1000              # Default weight in grams
  default-length: 20                # Default package length in cm
  default-width: 15                 # Default package width in cm
  default-height: 10                # Default package height in cm

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

---

## 🗄️ PHASE 2: Entities & Enums (20 min)

### File 3: `entity/ShippingProvider.java`

**Copilot Prompt:**
```java
Create ShippingProvider enum with value: GHN
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.entity;

public enum ShippingProvider {
    GHN  // Giao Hàng Nhanh
}
```

---

### File 4: `entity/ShipmentStatus.java`

**Copilot Prompt:**
```java
Create ShipmentStatus enum with values:
PENDING, PICKED_UP, IN_TRANSIT, OUT_FOR_DELIVERY, DELIVERED, FAILED_DELIVERY, CANCELLED
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.entity;

public enum ShipmentStatus {
    PENDING,            // Created, waiting for pickup
    PICKED_UP,          // Picked up from warehouse
    IN_TRANSIT,         // On the way
    OUT_FOR_DELIVERY,   // Out for delivery
    DELIVERED,          // Successfully delivered
    FAILED_DELIVERY,    // Failed delivery attempt
    CANCELLED           // Cancelled
}
```

---

### File 5: `entity/Shipment.java`

**Copilot Prompt:**
```java
Create Shipment entity with JPA annotations:
Fields:
- id (Long, auto-generated)
- shipmentNumber (String, unique, format: SHIP-YYYYMMDD-XXXX)
- orderId (String)
- orderNumber (String)
- provider (ShippingProvider enum)
- status (ShipmentStatus enum)
- trackingNumber (String, GHN tracking number)
- shippingFee (BigDecimal)
- codAmount (BigDecimal, cash on delivery amount)
- serviceType (String, e.g., "Standard", "Express")
- toName (String)
- toPhone (String)
- toAddress (String)
- toDistrictId (Integer)
- toWardCode (String)
- weight (Integer, in grams)
- length (Integer, in cm)
- width (Integer, in cm)
- height (Integer, in cm)
- currentLocation (String, nullable)
- pickedUpAt (LocalDateTime, nullable)
- deliveredAt (LocalDateTime, nullable)
- expectedDeliveryAt (LocalDateTime, nullable)
- createdAt (LocalDateTime)
- updatedAt (LocalDateTime)

Add @PrePersist and @PreUpdate for timestamps
Use @Column annotations for constraints
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.entity;

import jakarta.persistence.*;
import lombok.*;
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
    
    // Recipient info
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
    
    // Package dimensions
    @Column(nullable = false)
    private Integer weight;  // grams
    
    private Integer length;  // cm
    private Integer width;   // cm
    private Integer height;  // cm
    
    // Tracking
    @Column(length = 200)
    private String currentLocation;
    
    private LocalDateTime pickedUpAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime expectedDeliveryAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

---

## 📝 PHASE 3: DTOs (25 min)

### File 6: `dto/request/CalculateFeeRequest.java`

**Copilot Prompt:**
```java
Create CalculateFeeRequest DTO with validation:
- toDistrictId (required)
- toWardCode (required)
- weight (required, min 1)
- serviceTypeId (default 2 for Standard)
Add Lombok annotations
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalculateFeeRequest {
    
    @NotNull(message = "District ID is required")
    private Integer toDistrictId;
    
    @NotBlank(message = "Ward code is required")
    private String toWardCode;
    
    @NotNull(message = "Weight is required")
    @Min(value = 1, message = "Weight must be at least 1 gram")
    private Integer weight;
    
    private Integer serviceTypeId = 2;  // 1=Express, 2=Standard (default)
}
```

---

### File 7: `dto/request/CreateShipmentRequest.java`

**Copilot Prompt:**
```java
Create CreateShipmentRequest DTO with validation:
- orderId (required)
- orderNumber (required)
- toName (required)
- toPhone (required, pattern for Vietnamese phone)
- toAddress (required)
- toDistrictId (required)
- toWardCode (required)
- weight (required, min 1)
- length, width, height (optional)
- codAmount (required, min 0)
- note (optional)
Add Lombok annotations
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
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
```

---

### File 8: `dto/response/CalculateFeeResponse.java`

**Copilot Prompt:**
```java
Create CalculateFeeResponse DTO:
- provider (String)
- fee (BigDecimal)
- estimatedDays (Integer)
- serviceType (String)
- currency (String)
Add Lombok annotations
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.dto.response;

import lombok.*;
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
    private String currency = "VND";
}
```

---

### File 9: `dto/response/CreateShipmentResponse.java`

**Copilot Prompt:**
```java
Create CreateShipmentResponse DTO:
- shipmentId (Long)
- shipmentNumber (String)
- trackingNumber (String)
- provider (String)
- shippingFee (BigDecimal)
- estimatedDelivery (LocalDateTime)
Add Lombok annotations
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.dto.response;

import lombok.*;
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
```

---

### File 10: `dto/response/TrackingResponse.java`

**Copilot Prompt:**
```java
Create TrackingResponse DTO:
- trackingNumber (String)
- status (String)
- currentLocation (String)
- expectedDelivery (LocalDateTime)
- events (List of TrackingEvent)

Create inner class TrackingEvent:
- status (String)
- location (String)
- timestamp (LocalDateTime)
- description (String)

Add Lombok annotations
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingResponse {
    private String trackingNumber;
    private String status;
    private String currentLocation;
    private LocalDateTime expectedDelivery;
    private List<TrackingEvent> events;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TrackingEvent {
        private String status;
        private String location;
        private LocalDateTime timestamp;
        private String description;
    }
}
```

---

## ⚙️ PHASE 4: Configuration (15 min)

### File 11: `config/GHNConfig.java`

**Copilot Prompt:**
```java
Create GHNConfig with @Configuration and @Getter:
- apiUrl (from application.yml)
- token (from application.yml)
- shopId (from application.yml)
- fromDistrictId (from application.yml)
Use @Value annotations
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class GHNConfig {
    
    @Value("${ghn.api-url}")
    private String apiUrl;
    
    @Value("${ghn.token}")
    private String token;
    
    @Value("${ghn.shop-id}")
    private String shopId;
    
    @Value("${ghn.from-district-id}")
    private Integer fromDistrictId;
}
```

---

### File 12: `config/WebClientConfig.java`

**Copilot Prompt:**
```java
Create WebClientConfig with @Configuration:
- Create ghnWebClient WebClient bean
- Base URL from GHNConfig.apiUrl
- Default headers: Content-Type = application/json
- Default headers: Token and ShopId from GHNConfig
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {
    
    private final GHNConfig ghnConfig;
    
    @Bean
    public WebClient ghnWebClient() {
        return WebClient.builder()
            .baseUrl(ghnConfig.getApiUrl())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .defaultHeader("Token", ghnConfig.getToken())
            .defaultHeader("ShopId", ghnConfig.getShopId())
            .build();
    }
}
```

---

## 🔌 PHASE 5: GHN Client (30 min)

### File 13: `client/GHNClient.java`

**Copilot Prompt:**
```java
Create GHNClient with @Component:

Method 1: calculateFee(Integer toDistrictId, String toWardCode, Integer weight, Integer serviceTypeId)
- POST /v2/shipping-order/fee
- Request body: {
    service_type_id, from_district_id, to_district_id, to_ward_code, weight, insurance_value: 0
  }
- Return Map<String, Object> response
- Use WebClient.block() for synchronous call

Method 2: createOrder(String orderCode, String toName, String toPhone, String toAddress, 
           Integer toDistrictId, String toWardCode, Integer weight, Integer codAmount, String note)
- POST /v2/shipping-order/create
- Request body: {
    payment_type_id: 2 (receiver pays), required_note: "KHONGCHOXEMHANG",
    from_district_id, to_district_id, to_ward_code,
    to_name, to_phone, to_address,
    weight, service_type_id: 2, cod_amount, content, client_order_code
  }
- Return Map<String, Object> response
- Use WebClient.block()

Method 3: getTracking(String orderCode)
- POST /v2/shipping-order/detail
- Request body: { order_code }
- Return Map<String, Object> response
- Use WebClient.block()

Inject: WebClient, GHNConfig
Add @Slf4j for logging
Handle exceptions properly
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.client;

import com.ecommerce.shippingservice.config.GHNConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class GHNClient {
    
    private final WebClient ghnWebClient;
    private final GHNConfig ghnConfig;
    
    public Map<String, Object> calculateFee(
        Integer toDistrictId,
        String toWardCode,
        Integer weight,
        Integer serviceTypeId
    ) {
        log.info("Calculating GHN fee for district: {}, weight: {}", toDistrictId, weight);
        
        Map<String, Object> request = Map.of(
            "service_type_id", serviceTypeId,
            "from_district_id", ghnConfig.getFromDistrictId(),
            "to_district_id", toDistrictId,
            "to_ward_code", toWardCode,
            "weight", weight,
            "insurance_value", 0
        );
        
        return ghnWebClient.post()
            .uri("/v2/shipping-order/fee")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
    }
    
    public Map<String, Object> createOrder(
        String orderCode,
        String toName,
        String toPhone,
        String toAddress,
        Integer toDistrictId,
        String toWardCode,
        Integer weight,
        Integer codAmount,
        String note
    ) {
        log.info("Creating GHN order for: {}", orderCode);
        
        Map<String, Object> request = Map.of(
            "payment_type_id", 2,  // Receiver pays
            "required_note", "KHONGCHOXEMHANG",
            "from_district_id", ghnConfig.getFromDistrictId(),
            "to_district_id", toDistrictId,
            "to_ward_code", toWardCode,
            "to_name", toName,
            "to_phone", toPhone,
            "to_address", toAddress,
            "weight", weight,
            "service_type_id", 2,  // Standard
            "cod_amount", codAmount,
            "content", note != null ? note : "Order",
            "client_order_code", orderCode
        );
        
        return ghnWebClient.post()
            .uri("/v2/shipping-order/create")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
    }
    
    public Map<String, Object> getTracking(String orderCode) {
        log.info("Getting tracking for: {}", orderCode);
        
        Map<String, Object> request = Map.of("order_code", orderCode);
        
        return ghnWebClient.post()
            .uri("/v2/shipping-order/detail")
            .bodyValue(request)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
    }
}
```

---

## 💼 PHASE 6: Services (40 min)

### File 14: `service/GHNShippingService.java & ShippingService.java`

**Copilot Prompt:**
```java
Create GHNShippingService with @Service and @Transactional:

Method 1: calculateFee(CalculateFeeRequest request)
- Call ghnClient.calculateFee()
- Extract fee and estimatedDays from response.data
- Build and return CalculateFeeResponse

Method 2: createShipment(CreateShipmentRequest request)
- Generate shipmentNumber: SHIP-YYYYMMDD-XXXX
- Call ghnClient.createOrder() with codAmount converted to Integer
- Extract trackingNumber from response.data.order_code
- Build Shipment entity with all fields
- Save to repository
- Return CreateShipmentResponse with estimated delivery (now + 3 days)

Method 3: trackShipment(String trackingNumber)
- Find shipment by trackingNumber
- Call ghnClient.getTracking()
- Map GHN status to our ShipmentStatus
- Build TrackingResponse with events
- Return tracking response

Method 4: getShipmentByOrder(String orderId)
- Find shipment by orderId
- Return shipment or throw ShipmentNotFoundException

Helper: generateShipmentNumber()
- Format: SHIP-YYYYMMDD-XXXX (random 4 digits)

Helper: mapGHNStatus(String ghnStatus)
- Map GHN statuses to our ShipmentStatus enum
- ready_to_pick → PENDING
- picking → PICKED_UP
- transporting → IN_TRANSIT
- delivering → OUT_FOR_DELIVERY
- delivered → DELIVERED

Inject: GHNClient, ShipmentRepository
Add logging
```

---

## 🎮 PHASE 7: Controller (25 min)

### File 15: `controller/ShippingController.java`

**Copilot Prompt:**
```java
Create ShippingController with @RestController and @RequestMapping("/api/shipping"):

Endpoint 1: POST /calculate-fee
- @RequestBody CalculateFeeRequest (validated)
- Call shippingService.calculateFee()
- Return ResponseEntity<CalculateFeeResponse>

Endpoint 2: POST /create
- @RequestBody CreateShipmentRequest (validated)
- Call shippingService.createShipment()
- Return ResponseEntity<CreateShipmentResponse>

Endpoint 3: GET /track/{trackingNumber}
- @PathVariable String trackingNumber
- Call shippingService.trackShipment()
- Return ResponseEntity<TrackingResponse>

Endpoint 4: GET /order/{orderId}
- @PathVariable String orderId
- Call shippingService.getShipmentByOrder()
- Convert to response DTO
- Return ResponseEntity<CreateShipmentResponse>

Add @Slf4j for logging
Add exception handling
```

**Expected Code:**
```java
package com.ecommerce.shippingservice.controller;

import com.ecommerce.shippingservice.dto.request.*;
import com.ecommerce.shippingservice.dto.response.*;
import com.ecommerce.shippingservice.service.ShippingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipping")
@RequiredArgsConstructor
@Slf4j
public class ShippingController {
    
    private final ShippingService shippingService;
    
    @PostMapping("/calculate-fee")
    public ResponseEntity<CalculateFeeResponse> calculateFee(
        @Valid @RequestBody CalculateFeeRequest request
    ) {
        log.info("Calculating shipping fee for district: {}", request.getToDistrictId());
        CalculateFeeResponse response = shippingService.calculateFee(request);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/create")
    public ResponseEntity<CreateShipmentResponse> createShipment(
        @Valid @RequestBody CreateShipmentRequest request
    ) {
        log.info("Creating shipment for order: {}", request.getOrderNumber());
        CreateShipmentResponse response = shippingService.createShipment(request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/track/{trackingNumber}")
    public ResponseEntity<TrackingResponse> trackShipment(
        @PathVariable String trackingNumber
    ) {
        log.info("Tracking shipment: {}", trackingNumber);
        TrackingResponse response = shippingService.trackShipment(trackingNumber);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/order/{orderId}")
    public ResponseEntity<CreateShipmentResponse> getShipmentByOrder(
        @PathVariable String orderId
    ) {
        log.info("Getting shipment for order: {}", orderId);
        CreateShipmentResponse response = shippingService.getShipmentByOrderId(orderId);
        return ResponseEntity.ok(response);
    }
}
```

---

## 📡 API ENDPOINTS SUMMARY

### **Public Endpoints (Frontend & Other Services):**

```
1. POST /api/shipping/calculate-fee
   Body: {
     "toDistrictId": 1542,
     "toWardCode": "21012",
     "weight": 1000,
     "serviceTypeId": 2
   }
   Response: {
     "provider": "GHN",
     "fee": 25000,
     "estimatedDays": 3,
     "serviceType": "Standard",
     "currency": "VND"
   }

2. POST /api/shipping/create
   Body: {
     "orderId": "123",
     "orderNumber": "ORD-20240422-0001",
     "toName": "Nguyen Van A",
     "toPhone": "0901234567",
     "toAddress": "123 Le Loi, District 1",
     "toDistrictId": 1542,
     "toWardCode": "21012",
     "weight": 1000,
     "codAmount": 1000000,
     "note": "Call before delivery"
   }
   Response: {
     "shipmentId": 1,
     "shipmentNumber": "SHIP-20240422-0001",
     "trackingNumber": "GHNX12345678",
     "provider": "GHN",
     "shippingFee": 25000,
     "estimatedDelivery": "2024-04-25T10:00:00"
   }

3. GET /api/shipping/track/{trackingNumber}
   Response: {
     "trackingNumber": "GHNX12345678",
     "status": "IN_TRANSIT",
     "currentLocation": "Hub HCMC",
     "expectedDelivery": "2024-04-25T10:00:00",
     "events": [
       {
         "status": "PICKED_UP",
         "location": "Warehouse Hanoi",
         "timestamp": "2024-04-22T10:00:00",
         "description": "Package picked up"
       }
     ]
   }

4. GET /api/shipping/order/{orderId}
   Response: CreateShipmentResponse
```

---

## 🧪 TESTING

### **Test Calculate Fee:**
```bash
curl -X POST http://localhost:8088/api/shipping/calculate-fee \
  -H "Content-Type: application/json" \
  -d '{
    "toDistrictId": 1542,
    "toWardCode": "21012",
    "weight": 1000
  }'

# Expected: 200 OK with fee response
```

### **Test Create Shipment:**
```bash
curl -X POST http://localhost:8088/api/shipping/create \
  -H "Content-Type: application/json" \
  -d '{
    "orderId": "123",
    "orderNumber": "ORD-20240422-0001",
    "toName": "Nguyen Van A",
    "toPhone": "0901234567",
    "toAddress": "123 Le Loi",
    "toDistrictId": 1542,
    "toWardCode": "21012",
    "weight": 1000,
    "codAmount": 1000000
  }'

# Expected: 200 OK with tracking number
```

---

## ✅ IMPLEMENTATION CHECKLIST

```
Setup:
[ ] pom.xml dependencies added
[ ] application.yml configured
[ ] GHN account registered at https://sship.vn
[ ] GHN test token obtained
[ ] Database shipping_db created

Entities & Enums:
[ ] ShippingProvider enum created
[ ] ShipmentStatus enum created
[ ] Shipment entity created with all fields

DTOs:
[ ] CalculateFeeRequest created
[ ] CreateShipmentRequest created
[ ] CalculateFeeResponse created
[ ] CreateShipmentResponse created
[ ] TrackingResponse created

Configuration:
[ ] GHNConfig created
[ ] WebClientConfig created

Client:
[ ] GHNClient created with 3 methods
[ ] calculateFee tested
[ ] createOrder tested
[ ] getTracking tested

Services:
[ ] GHNShippingService created
[ ] ShippingService created
[ ] All methods implemented

Controller:
[ ] ShippingController created
[ ] All 4 endpoints implemented

Database:
[ ] shipments table created
[ ] Indexes added

Integration:
[ ] Can calculate shipping fee
[ ] Can create shipment
[ ] Can track shipment
[ ] GHN API responds correctly
```

---

**TOTAL: 15 Files | 2-3 Hours | Ready for Frontend Integration! 🚀**