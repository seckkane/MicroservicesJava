package com.techie.microservices.inventory.exception;


public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String skuCode) {
        super("Produit non trouve avec ce code : " + skuCode);
    }
}
