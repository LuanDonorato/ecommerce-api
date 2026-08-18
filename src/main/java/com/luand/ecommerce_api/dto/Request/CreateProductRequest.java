package com.luand.ecommerce_api.dto.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateProductRequest(@NotNull String name,
                                   @NotNull @Positive Double price,
                                   @NotNull @Positive Integer quantity) {
}
