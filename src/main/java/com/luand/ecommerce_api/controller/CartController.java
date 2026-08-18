package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.dto.CartItemDto;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<CartItemDto> getCart(@PathVariable Long userId) throws NotFoundException {
        return cartService.getCart(userId);
    }
}
