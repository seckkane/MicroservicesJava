package com.techie.microservices.order.dto;

import java.math.BigDecimal;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record OrderRequest(
        Long id,
        String orderNumber,
        @NotEmpty(message = "Le code produit est obligatoire")
        String skuCode,
        BigDecimal price,
        @NotNull(message = "La quantité est obligatoire")
        @Positive(message = "La quantité doit être positive")
        Integer quantity
        // UserDetails userDetails
) {
    public record UserDetails(
            String email,
            String firstName,
            String lastName
    ) {}
}
