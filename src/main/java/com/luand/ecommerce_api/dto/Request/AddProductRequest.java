package com.luand.ecommerce_api.dto.Request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddProductRequest(@NotNull Long productId,
                                @NotNull @Positive Integer quantity) {
}
