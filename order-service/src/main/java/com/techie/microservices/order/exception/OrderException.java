package com.techie.microservices.order.exception;

public class OrderException extends RuntimeException {
    public OrderException(String message) {
        super(message);
    }
}
