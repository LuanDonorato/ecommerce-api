package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.dto.Request.CreateProductRequest;
import com.luand.ecommerce_api.entity.ProductEntity;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductEntity> findAll() {
        return productService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductEntity createProduct(@RequestBody CreateProductRequest request) throws AlreadyExistsException {
        return productService.createProduct(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
