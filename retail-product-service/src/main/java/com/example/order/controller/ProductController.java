package com.example.order.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/product")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @GetMapping("/details")
    public String getProductDetails(@RequestParam(name = "productId", required = true) String productId) {
        logger.info("Received request for product details: {}", productId);

        // Simulate product lookup
        try {
            logger.debug("Simulating product lookup for {}", productId);
            Thread.sleep(20);
        } catch (InterruptedException e) {
            logger.error("Product lookup interrupted for {}", productId, e);
            Thread.currentThread().interrupt();
            return "Error processing product lookup";
        }

        String response = "Product " + productId + ": Retail Item, Price $99.99";
        logger.info("Returning product details: {}", response);
        return response;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Product Service is UP";
    }
}