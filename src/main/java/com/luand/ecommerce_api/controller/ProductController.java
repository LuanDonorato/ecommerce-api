package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.dto.Request.CreateProductRequest;
import com.luand.ecommerce_api.entity.ProductEntity;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Oporeções de gerenciamento dos produtos")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Buscar produtos", description = "Retorna lista de todos os produtos")
    public List<ProductEntity> findAll() {
        return productService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar produto")
    public ProductEntity createProduct(@RequestBody CreateProductRequest request) throws AlreadyExistsException {
        return productService.createProduct(request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remover produto", description = "Remove produto a partir do id")
    public void deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
    }
}
