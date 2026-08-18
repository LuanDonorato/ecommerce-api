package com.luand.ecommerce_api.service;

import com.luand.ecommerce_api.dto.CartItemDto;
import com.luand.ecommerce_api.entity.CartEntity;
import com.luand.ecommerce_api.entity.CartItemEntity;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    public List<CartItemDto> getCart(Long userId) throws NotFoundException {

        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Carrinho não encontrado"));

        List<CartItemEntity> items = cart.getItems();

        List<CartItemDto> list = new ArrayList<>();

        for (CartItemEntity item : items) {
            CartItemDto dto = new CartItemDto(
                    item.getProduct().getName(),
                    item.getProduct().getPrice(),
                    item.getQuantity()
            );
            list.add(dto);
        }
        return list;
    }
}
