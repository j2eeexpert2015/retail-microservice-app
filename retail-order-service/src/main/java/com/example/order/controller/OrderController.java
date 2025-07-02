package com.example.order.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/order")
public class OrderController {

    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    // private final RestTemplate restTemplate;
    private final RestClient restClient;

    @Autowired
    public OrderController(/*RestTemplate restTemplate,*/ RestClient restClient) {
        // this.restTemplate = restTemplate;
        this.restClient = restClient;
    }

    @GetMapping("/place")
    public ResponseEntity<String> placeOrder(@RequestParam(name = "productId", required = true) String productId) {
        logger.info("Placing order for product: {}", productId);

        try {
            logger.debug("Calling product service for {}", productId);
            String productResponse = restClient.get()
                    .uri("/product/details?productId={productId}", productId)
                    .retrieve()
                    .body(String.class);

            String response = "Order placed for product " + productId + ". Product details: " + productResponse;
            logger.info("Order processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Failed to call product service", e);
            return ResponseEntity.badRequest().body("Failed to retrieve product details");
        }
    }

    @GetMapping("/health")
    public String healthCheck() {
        try {
            String productHealth = restClient.get()
                    .uri("/product/health")
                    .retrieve()
                    .body(String.class);
            return "Order Service is UP. Product Service: " + productHealth;
        } catch (Exception e) {
            return "Order Service is UP but cannot connect to Product Service";
        }
    }
}