package com.example.order.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    private final RestTemplate restTemplate;

    @Autowired
    public OrderController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestParam(name = "productId", required = true) String productId) {
        logger.info("Placing order for product: {}", productId);

        // Simulate order processing
        try {
            logger.debug("Simulating order processing");
            Thread.sleep(50);
        } catch (InterruptedException e) {
            logger.error("Order processing interrupted", e);
            Thread.currentThread().interrupt();
            return ResponseEntity.internalServerError().body("Order processing interrupted");
        }

        // Call external product service
        try {
            logger.debug("Calling product service for {}", productId);
            String productResponse = restTemplate.getForObject(
                    "http://localhost:8081/product/details?productId={productId}",
                    String.class,
                    productId
            );

            String response = "Order placed for product " + productId + ". Product details: " + productResponse;
            logger.info("Order processed successfully");
            return ResponseEntity.ok(response);
        } catch (RestClientException e) {
            logger.error("Failed to call product service", e);
            return ResponseEntity.badRequest().body("Failed to retrieve product details");
        }
    }

    @GetMapping("/health")
    public String healthCheck() {
        try {
            String productHealth = restTemplate.getForObject(
                    "http://localhost:8081/product/health",
                    String.class
            );
            return "Order Service is UP. Product Service: " + productHealth;
        } catch (Exception e) {
            return "Order Service is UP but cannot connect to Product Service";
        }
    }
}