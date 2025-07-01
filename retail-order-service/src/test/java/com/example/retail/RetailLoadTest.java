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
            .baseUrl("http://localhost:8080") // Order Service
            .acceptHeader("application/json")
            .userAgentHeader("Gatling-Java-Load-Test")
            .shareConnections()
            .maxConnectionsPerHost(1000); // Removed: .connectionTimeout(...)

    // 2. Random Data Generation
    private static String randomProductId() {
        String[] categories = {"ELEC", "CLOTH", "FOOD", "HOME"};
        String category = categories[ThreadLocalRandom.current().nextInt(categories.length)];
        return category + "-" + ThreadLocalRandom.current().nextInt(1000, 9999);
    }

    // 3. Scenario Definition
    private ChainBuilder placeOrder =
            exec(http("Place Order")
                    .get("/order/place")
                    .queryParam("productId", "#{randomProductId}")
                    .check(status().is(200))
                    .check(jsonPath("$.status").is("CONFIRMED")))
                    .pause(Duration.ofMillis(100), Duration.ofMillis(300));

    @Override
    public void before() {
        System.out.println("Starting load test comparing virtual vs platform threads");
    }

    // 4. Load Injection Profile
    {
        ScenarioBuilder scenario = scenario("Virtual Thread Load Test")
                .exec(session -> session.set("randomProductId", randomProductId()))
                .exec(placeOrder);

        setUp(
                scenario.injectOpen(
                        rampUsers(100).during(10),
                        constantUsersPerSec(100).during(60)
                )
        )
                .protocols(httpProtocol)
                .assertions(
                        global().responseTime().percentile3().lte(500),
                        global().failedRequests().percent().lte(1.0)
                );
    }
}
