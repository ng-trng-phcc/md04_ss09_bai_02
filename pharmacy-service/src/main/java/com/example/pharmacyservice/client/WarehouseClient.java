package com.example.pharmacyservice.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class WarehouseClient {

    private final RestTemplate restTemplate;

    private static final String WAREHOUSE_STOCK_URL = "http://WAREHOUSE-SERVICE/api/v1/warehouse/stock/{productId}";
    private static final String WAREHOUSE_CHECK_URL = "http://WAREHOUSE-SERVICE/api/v1/warehouse/check";

    /**
     * Gọi warehouse-service để kiểm tra tồn kho.
     * Khi warehouse phản hồi chậm/sập, CircuitBreaker warehouseCB sẽ mở (failure-rate 50%, open 20s) để tránh treo máy tính tiền.
     */
    @CircuitBreaker(name = "warehouseCB", fallbackMethod = "fallback")
    public Map<String, Object> checkStock(String productId) {
        log.info("Calling warehouse-service for productId: {}", productId);
        return restTemplate.getForObject(WAREHOUSE_STOCK_URL, Map.class, productId);
    }

    @CircuitBreaker(name = "warehouseCB", fallbackMethod = "fallbackCheck")
    public Map<String, Object> checkStockWithQuantity(String productId, int quantity) {
        String url = WAREHOUSE_CHECK_URL + "?productId=" + productId + "&quantity=" + quantity;
        return restTemplate.getForObject(url, Map.class);
    }

    @SuppressWarnings("unused")
    public Map<String, Object> fallback(String productId, Throwable ex) {
        log.warn("warehouseCB fallback for productId={}, reason={}", productId, ex.toString());
        return Map.of(
                "productId", productId,
                "available", false,
                "fallback", true,
                "message", "Kho tổng tạm thời không khả dụng, vui lòng thử lại sau 20 giây (CircuitBreaker OPEN)"
        );
    }

    @SuppressWarnings("unused")
    public Map<String, Object> fallbackCheck(String productId, int quantity, Throwable ex) {
        log.warn("warehouseCB fallbackCheck for productId={}, quantity={}, reason={}", productId, quantity, ex.toString());
        return Map.of(
                "productId", productId,
                "quantity", quantity,
                "available", false,
                "fallback", true,
                "message", "Kho tổng phản hồi chậm/sập, mạch đã ngắt 20s để tránh treo máy tính tiền"
        );
    }
}
