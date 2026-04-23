package com.ecommerce.shipping.client;

import com.ecommerce.shipping.config.GHNConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashMap;
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
        // Map.of supports only up to 10 key-value pairs; this request needs more fields.
        Map<String, Object> request = new HashMap<>();
        request.put("payment_type_id", 2);
        request.put("required_note", "KHONGCHOXEMHANG");
        request.put("from_district_id", ghnConfig.getFromDistrictId());
        request.put("to_district_id", toDistrictId);
        request.put("to_ward_code", toWardCode);
        request.put("to_name", toName);
        request.put("to_phone", toPhone);
        request.put("to_address", toAddress);
        request.put("weight", weight);
        request.put("service_type_id", 2);
        request.put("cod_amount", codAmount);
        request.put("content", note != null ? note : "Order");
        request.put("client_order_code", orderCode);

        return ghnWebClient.post()
                .uri("/v2/shipping-order/create")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }

    public Map<String, Object> getTracking(String orderCode) {
        log.info("Getting GHN tracking for: {}", orderCode);
        Map<String, Object> request = Map.of("order_code", orderCode);

        return ghnWebClient.post()
                .uri("/v2/shipping-order/detail")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
    }
}

