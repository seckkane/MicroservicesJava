package com.techie.microservices.inventory.exception;

public class ProductOutOfStockException extends RuntimeException {
    public ProductOutOfStockException(String skuCode, Integer quantity, Integer inStock) {
        super("Quantité indisponible pour '" + skuCode + "' : " + quantity + ". En stock ["+ inStock +"]");
    }
}
