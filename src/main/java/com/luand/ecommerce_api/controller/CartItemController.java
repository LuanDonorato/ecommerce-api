package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.dto.Request.AddProductRequest;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.service.CartItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
@Tag(name = "Itens do carrinho", description = "Oporeções para gerenciar itens no carrinho")
public class CartItemController {

    private final CartItemService cartItemService;

    @PostMapping("/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Adicionar produto ao carrinho")
    public void addProductToCartItem(@PathVariable Long userId, @Valid @RequestBody AddProductRequest request) throws NotFoundException {
        cartItemService.addProductToCartItem(userId, request.productId(), request.quantity());
    }

    @Operation(summary = "Remover produto do carrinho")
    @DeleteMapping("/{userId}/products/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeProductOfCartItem(@PathVariable Long userId, @PathVariable Long productId, @RequestParam Integer quantity ) throws NotFoundException {
        cartItemService.removeProductOfCartItem(userId, productId, quantity);
    }
}
