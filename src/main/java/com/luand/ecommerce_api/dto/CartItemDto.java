package com.luand.ecommerce_api.dto;

public record CartItemDto(String productName, Double price, Integer quantity) {
}
