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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartItemService {

    private final UserRepository userRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    public void addProductToCartItem(Long userId, Long productId, Integer quantity) throws NotFoundException {

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        Optional <CartEntity> cartOptional = cartRepository.findByUserId(userId);

        CartEntity cart;

        if (cartOptional.isPresent()) {
            cart = cartOptional.get();
        }

        else {
            cart = createCart(userId);
        }

        Optional<CartItemEntity> cartItemExistence = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);

        if (product.getQuantity() < quantity) {
            throw new NotFoundException("Quantidade no estoque insuficiente");
        }

        else if (cartItemExistence.isPresent()) {
            CartItemEntity item = cartItemExistence.get();
            Integer newQuantity = item.getQuantity() + quantity;
            item.setQuantity(newQuantity);
            cartItemRepository.save(item);
        }

        else {
            cartItemRepository.save(CartItemEntity.builder()
                    .product(product)
                    .cart(cart)
                    .quantity(quantity)
                    .build());
        }

        Integer newProductQuantity = product.getQuantity() - quantity;
        product.setQuantity(newProductQuantity);
        productRepository.save(product);
    }

    public void removeProductOfCartItem(Long userId, Long productId, Integer quantity) throws NotFoundException {

        CartEntity cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Carrinho não encontrado"));

        ProductEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado"));

        CartItemEntity item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new NotFoundException("Produto não encontrado no carrinho"));

        int newProductQuantity = product.getQuantity() + item.getQuantity();

        if (quantity >= item.getQuantity()) {
            cartItemRepository.delete(item);
        }

        else {
            item.setQuantity(item.getQuantity() - quantity);
            cartItemRepository.save(item);
            newProductQuantity = product.getQuantity() + quantity;
        }
        product.setQuantity(newProductQuantity);
        productRepository.save(product);
    }

    private CartEntity createCart(Long userId) throws NotFoundException {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        CartEntity cart = new CartEntity();
        cart.setUser(user);
        return cartRepository.save(cart);
    }
}
