package com.techie.microservices.order.service;

public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(String skuCode, Integer quantity) {
        super("Quantité indisponible pour '" + skuCode + "' : " + quantity);
    }
}
