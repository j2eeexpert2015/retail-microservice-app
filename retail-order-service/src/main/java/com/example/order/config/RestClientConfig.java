package com.example.order.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.util.concurrent.Executors;

@Configuration
public class RestClientConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestClientConfig.class);

    @Bean
    public RestClient restClient(@Value("${product.service.url}") String baseUrl,
                                 @Value("${spring.threads.virtual.enabled}") boolean isVirtualThreadEnabled) {
        logger.info("Configuring RestClient with base URL: {}", baseUrl);
        RestClient.Builder builder = RestClient.builder().baseUrl(baseUrl);
        if (isVirtualThreadEnabled) {
            builder.requestFactory(new JdkClientHttpRequestFactory(
                    HttpClient.newBuilder().executor(Executors.newVirtualThreadPerTaskExecutor()).build()
            ));
        }
        return builder.build();
    }
}