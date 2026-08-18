package com.luand.ecommerce_api.service;

import com.luand.ecommerce_api.dto.Request.CreateProductRequest;
import com.luand.ecommerce_api.entity.ProductEntity;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    @Captor
    private ArgumentCaptor<ProductEntity> productCaptor;

    @Test
    void deveriaRetornarListaDeProdutos() {

        List<ProductEntity> products = List.of(
                ProductEntity.builder().name("ProdutoTeste1").price(12.99).quantity(10).build(),
                ProductEntity.builder().name("ProdutoTeste2").price(15.50).quantity(18).build()
        );

        BDDMockito.given(repository.findAll()).willReturn(products);

        List<ProductEntity> result = service.findAll();

        Assertions.assertEquals(products, result);
        BDDMockito.then(repository).should().findAll();
    }

    @Test
    void deveriaCriarProdutoQuandoNaoExistir() throws AlreadyExistsException {

        CreateProductRequest request = new CreateProductRequest("ProdutoTeste", 12.99, 10);

        BDDMockito.given(repository.findByName(request.name())).willReturn(Optional.empty());

        service.createProduct(request);

        BDDMockito.then(repository).should().save(productCaptor.capture());
        ProductEntity productSaved = productCaptor.getValue();
        Assertions.assertEquals(request.name(), productSaved.getName());
        Assertions.assertEquals(request.price(), productSaved.getPrice());
        Assertions.assertEquals(request.quantity(), productSaved.getQuantity());
    }

    @Test
    void deveriaLancarAlreadyExistsExceptionAoTentarCriarProduto() {

        CreateProductRequest request = new CreateProductRequest("ProdutoTeste", 12.99, 10);

        BDDMockito.given(repository.findByName(request.name())).willReturn(Optional.of(new ProductEntity()));

        AlreadyExistsException exception = Assertions.assertThrows(
                AlreadyExistsException.class, () -> service.createProduct(request));

        Assertions.assertEquals("Produto já cadastrado", exception.getMessage());
        BDDMockito.then(repository).should(BDDMockito.never()).save(BDDMockito.any());
    }

    @Test
    void deveveriaApagarProduto() {

        Long id = 1L;

        service.deleteProduct(id);

        BDDMockito.then(repository).should().deleteById(id);
    }

}