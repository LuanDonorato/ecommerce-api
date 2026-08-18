package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.dto.CartItemDto;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/carts")
@Tag(name = "Carrinho", description = "Oporeção para gerenciamento do carrinho")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Buscar carrinho", description = "Retorna o carrinho do usuário a partir do userId")
    public List<CartItemDto> getCart(@PathVariable Long userId) throws NotFoundException {
        return cartService.getCart(userId);
    }
}
