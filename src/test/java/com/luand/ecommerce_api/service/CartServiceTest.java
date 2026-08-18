package com.luand.ecommerce_api.service;


import com.luand.ecommerce_api.dto.CartItemDto;
import com.luand.ecommerce_api.entity.CartEntity;
import com.luand.ecommerce_api.entity.CartItemEntity;
import com.luand.ecommerce_api.entity.ProductEntity;
import com.luand.ecommerce_api.entity.UserEntity;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.repository.CartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Test
    void deveriaRetornarListaDeProdutos() throws NotFoundException {

        ProductEntity product = ProductEntity.builder().name("Produto1").price(12.00).build();
        ProductEntity product2 = ProductEntity.builder().name("Produto2").price(12.00).build();

        List<CartItemEntity> items = List.of(
                CartItemEntity.builder().product(product).quantity(5).build(),
                CartItemEntity.builder().product(product2).quantity(5).build()
        );

        CartItemDto item1 = new CartItemDto("Produto1", 12.00, 5);
        CartItemDto item2 = new CartItemDto("Produto2", 12.00, 5);

        List<CartItemDto> listDto = List.of(item1, item2);

        UserEntity user = UserEntity.builder().id(1L).build();
        CartEntity cart = new CartEntity(1L, user,items);

        BDDMockito.given(cartRepository.findByUserId(user.getId())).willReturn(Optional.of(cart));

        List<CartItemDto> result = cartService.getCart(user.getId());

        Assertions.assertEquals(listDto, result);
    }

    @Test
    void deveriaLancarNotFoundExceptionAoNaoEncontrarCarrinhoDoUsuario() {

        Long userId = 1L;

        BDDMockito.given(cartRepository.findByUserId(userId)).willReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> cartService.getCart(userId)
        );

        Assertions.assertEquals("Carrinho não encontrado", exception.getMessage());
    }

}