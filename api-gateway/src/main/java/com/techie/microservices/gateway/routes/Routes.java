package com.techie.microservices.gateway.routes;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.filter.CircuitBreakerFilterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;



@Configuration
public class Routes { //8090 Api Gateway

    @Bean
    public RouterFunction<ServerResponse> productServiceRoute() {
        return GatewayRouterFunctions.route("product_service") //8080 Product
                .route(RequestPredicates.path("/api/product"), HandlerFunctions.http("http://localhost:8080"))
                //.filter(CircuitBreakerFilterFunctions.circuitBreaker("productServiceCircuitBreaker",
                //        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> orderServiceRoute() {
        return GatewayRouterFunctions.route("order_service") //8082 Order
                .route(RequestPredicates.path("/api/order"), HandlerFunctions.http("http://localhost:8082"))
                //.filter(CircuitBreakerFilterFunctions.circuitBreaker("orderServiceCircuitBreaker",
                //        URI.create("forward:/fallbackRoute")))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> inventoryServiceRoute() {
        return GatewayRouterFunctions.route("inventory_service")
                .route(RequestPredicates.path("/api/inventory"), HandlerFunctions.http("http://localhost:8086"))
                //.filter(CircuitBreakerFilterFunctions.circuitBreaker("inventoryServiceCircuitBreaker",
                //        URI.create("forward:/fallbackRoute")))
                .build();
    }
}
