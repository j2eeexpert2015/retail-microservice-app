package com.example.retail;

import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class RetailLoadTest extends Simulation {

    // 1. HTTP Configuration
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("http://localhost:8080") // Order Service base URL
            .acceptHeader("application/json")
            .userAgentHeader("Gatling-Java-Load-Test")
            .shareConnections()
            .maxConnectionsPerHost(1000);

    // 2. Random Product ID Generator
    private static String randomProductId() {
        String[] categories = {"ELEC", "CLOTH", "FOOD", "HOME"};
        String category = categories[ThreadLocalRandom.current().nextInt(categories.length)];
        return category + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    // 3. Chain: Simulate placing an order
    private ChainBuilder placeOrder =
            exec(session -> session.set("randomProductId", randomProductId()))
                    .exec(http("Place Order")
                            .get("/order/place")
                            .queryParam("productId", "#{randomProductId}")
                            .queryParam("quantity", "1")
                            .check(status().is(200))
                            .check(substring("Order placed for product").exists()))
                    .pause(Duration.ofMillis(100), Duration.ofMillis(300));

    // 4. Before simulation starts
    @Override
    public void before() {
        System.out.println("Starting load test comparing virtual vs platform threads");
    }

    // 5. Load profile setup (no assertions)
    {
        ScenarioBuilder scenario = scenario("Virtual Thread Load Test")
                .exec(placeOrder);

        setUp(
                scenario.injectOpen(
                        rampUsers(100).during(10),
                        constantUsersPerSec(100).during(60)
                )
        )
                .protocols(httpProtocol);
    }
}
