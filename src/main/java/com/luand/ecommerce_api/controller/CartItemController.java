package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.dto.Request.AddProductRequest;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.service.CartItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void addProductToCartItem(@PathVariable Long userId, @Valid @RequestBody AddProductRequest request) throws NotFoundException {
        cartItemService.addProductToCartItem(userId, request.productId(), request.quantity());
    }

    @DeleteMapping("/{userId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProductOfCartItem(@PathVariable Long userId, @PathVariable Long productId, @RequestParam Integer quantity ) throws NotFoundException {
        cartItemService.removeProductOfCartItem(userId, productId, quantity);
    }
}
