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

        String response = "Product " + productId + ": Retail Item, Price $99.99";
        logger.info("Returning product details: {}", response);
        return response;
    }

    @GetMapping("/health")
    public String healthCheck() {
        return "Product Service is UP";
    }
}