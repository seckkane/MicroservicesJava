package com.techie.microservices.order.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@FeignClient(value = "inventory", url = "${inventory.url}")
@CircuitBreaker(name = "inventory", fallbackMethod = "fallbackMethod") // inventoty = name of the reslience in app. prop
@Retry(name = "inventory")
public interface InventoryClient {

    Logger log = LoggerFactory.getLogger(InventoryClient.class);

   @RequestMapping(method = RequestMethod.GET, value = "api/inventory")
    boolean isInstock(@RequestParam String skuCode, @RequestParam Integer quantity);

    default boolean fallbackMethod(String code, Integer quantity, Throwable throwable) {
        log.info("Cannot get inventory for skucode {}, failure reason: {}", code, throwable.getMessage());
        return false;
    }
}