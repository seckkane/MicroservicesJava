package com.techie.microservices.inventory.dto;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {}
