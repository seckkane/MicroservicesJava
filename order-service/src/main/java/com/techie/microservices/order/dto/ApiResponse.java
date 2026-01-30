package com.techie.microservices.order.dto;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {
    public String getMessage() {
        return this.message;
    }
}
