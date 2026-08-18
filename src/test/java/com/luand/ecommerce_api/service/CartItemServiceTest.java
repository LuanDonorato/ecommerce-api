package com.luand.ecommerce_api.service;

import com.luand.ecommerce_api.entity.CartEntity;
import com.luand.ecommerce_api.entity.CartItemEntity;
import com.luand.ecommerce_api.entity.ProductEntity;
import com.luand.ecommerce_api.entity.UserEntity;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.repository.CartItemRepository;
import com.luand.ecommerce_api.repository.CartRepository;
import com.luand.ecommerce_api.repository.ProductRepository;
import com.luand.ecommerce_api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class CartItemServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartItemService service;

    @Captor
    private ArgumentCaptor<CartEntity> cartCaptor;

    @Captor
    private ArgumentCaptor<CartItemEntity> cartItemCaptor;

    @Test
    void deveriaAdicionarProdutoAoCarrinho() throws NotFoundException {

        ProductEntity product = ProductEntity.builder().id(1L).quantity(10).build();
        UserEntity user = UserEntity.builder().id(1L).build();
        CartEntity cart = new CartEntity(1L, user, List.of());

        Integer quantity = 3;

        BDDMockito.given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        BDDMockito.given(cartRepository.findByUserId(user.getId())).willReturn(Optional.of(cart));
        BDDMockito.given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).willReturn(Optional.empty());

        service.addProductToCartItem(user.getId(), product.getId(), quantity);

        BDDMockito.then(cartItemRepository).should().save(cartItemCaptor.capture());
        CartItemEntity cartItemSaved = cartItemCaptor.getValue();
        Assertions.assertEquals(product, cartItemSaved.getProduct());
        Assertions.assertEquals(cart, cartItemSaved.getCart());
        Assertions.assertEquals(quantity, cartItemSaved.getQuantity());
    }

    @Test
    void deveriaSalvarNovaQuantidadeDoProdutoExistenteNoCarrinho() throws NotFoundException {

        ProductEntity product = ProductEntity.builder().id(1L).quantity(10).build();
        UserEntity user = UserEntity.builder().id(1L).build();
        CartEntity cart = new CartEntity(1L, user, List.of());
        CartItemEntity cartItem = CartItemEntity.builder().quantity(10).build();

        Integer quantity = 3;

        BDDMockito.given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        BDDMockito.given(cartRepository.findByUserId(user.getId())).willReturn(Optional.of(cart));
        BDDMockito.given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).willReturn(Optional.of(cartItem));

        service.addProductToCartItem(user.getId(), product.getId(), quantity);

        BDDMockito.then(cartItemRepository).should().save(cartItemCaptor.capture());
        CartItemEntity cartItemSaved = cartItemCaptor.getValue();
        Assertions.assertEquals(13, cartItemSaved.getQuantity());
        Assertions.assertEquals(7, product.getQuantity());
    }

    @Test
    void deveriaLancarNotFoundExceptionAoTentarSalvarProdutoNoCarrinho() {

        Long userId = 1L;
        Long productId = 1L;
        int quantity = 3;

        BDDMockito.given(productRepository.findById(productId)).willReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> service.addProductToCartItem(userId, productId, quantity));
        Assertions.assertEquals("Produto não encontrado", exception.getMessage());

        BDDMockito.then(cartItemRepository).should(BDDMockito.never()).save(BDDMockito.any());
        BDDMockito.then(productRepository).should(BDDMockito.never()).save(BDDMockito.any());
    }

    @Test
    void deveriaLancarNotFoundExceptionAQuantidadeInsuficienteNoEstoque() {

        ProductEntity product = ProductEntity.builder().id(1L).quantity(10).build();
        UserEntity user = UserEntity.builder().id(1L).build();
        CartEntity cart = new CartEntity(1L, user, List.of());
        CartItemEntity cartItem = CartItemEntity.builder().quantity(10).build();

        int quantity = 15;

        BDDMockito.given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        BDDMockito.given(cartRepository.findByUserId(user.getId())).willReturn(Optional.of(cart));
        BDDMockito.given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).willReturn(Optional.of(cartItem));

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> service.addProductToCartItem(user.getId(), product.getId(), quantity));

        Assertions.assertEquals("Quantidade no estoque insuficiente", exception.getMessage());
        BDDMockito.then(cartItemRepository).should(BDDMockito.never()).save(BDDMockito.any());
        BDDMockito.then(productRepository).should(BDDMockito.never()).save(BDDMockito.any());
    }

    @Test
    void deveriaCriarCarrinhoQuandoUsuarioNaoTiver() throws NotFoundException {

        ProductEntity product = ProductEntity.builder().id(1L).quantity(10).build();
        UserEntity user = UserEntity.builder().id(1L).build();
        CartEntity cart = new CartEntity(1L, user, List.of());

        Integer quantity = 3;

        BDDMockito.given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        BDDMockito.given(cartRepository.findByUserId(user.getId())).willReturn(Optional.empty());
        BDDMockito.given(userRepository.findById(user.getId())).willReturn(Optional.of(user));
        BDDMockito.given(cartRepository.save(BDDMockito.any(CartEntity.class))).willReturn(cart);
        BDDMockito.given(cartItemRepository.findByCartIdAndProductId(cart.getId(), product.getId())).willReturn(Optional.empty());

        service.addProductToCartItem(user.getId(), product.getId(), quantity);

        BDDMockito.then(cartRepository).should().save(cartCaptor.capture());
        CartEntity cartSaved = cartCaptor.getValue();
        Assertions.assertEquals(user, cartSaved.getUser());
    }

    @Test
    void deveriaLancarNotFoundExceptionAoTentarCriarCarrinho() {

        ProductEntity product = ProductEntity.builder().id(1L).quantity(10).build();
        UserEntity user = UserEntity.builder().id(1L).build();

        Integer quantity = 3;

        BDDMockito.given(productRepository.findById(product.getId())).willReturn(Optional.of(product));
        BDDMockito.given(cartRepository.findByUserId(user.getId())).willReturn(Optional.empty());

        BDDMockito.given(userRepository.findById(user.getId())).willReturn(Optional.empty());

        NotFoundException exception = Assertions.assertThrows(
                NotFoundException.class, () -> service.addProductToCartItem(user.getId(), product.getId(), quantity));

        Assertions.assertEquals("Usuário não encontrado", exception.getMessage());
        BDDMockito.then(cartRepository).should(BDDMockito.never()).save(BDDMockito.any());
    }

}