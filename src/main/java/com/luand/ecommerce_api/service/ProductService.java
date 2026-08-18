package com.luand.ecommerce_api.service;

import com.luand.ecommerce_api.dto.Request.CreateProductRequest;
import com.luand.ecommerce_api.entity.ProductEntity;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.repository.ProductRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public List<ProductEntity> findAll() {
        return productRepository.findAll();
    }

    public ProductEntity createProduct(@Valid CreateProductRequest request) throws AlreadyExistsException {

         if (productRepository.findByName(request.name()).isPresent()) {
            throw new AlreadyExistsException("Produto já cadastrado");
        }

        return productRepository.save(ProductEntity.builder()
                .name(request.name())
                .price(request.price())
                .quantity(request.quantity())
                .build());
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
