package com.luand.ecommerce_api.controller;


import com.luand.ecommerce_api.config.SecurityConfig;
import com.luand.ecommerce_api.config.TokenConfig;
import com.luand.ecommerce_api.dto.Request.CreateProductRequest;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    ProductService service;

    @MockitoBean
    private TokenConfig tokenConfig;

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveriaRetornar200AoChamarListaDeProdutos() throws Exception {

        var response = mvc.perform(
                MockMvcRequestBuilders.get("/products")
        ).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveriaRetonar201AoCriarProdutoSemErro() throws Exception {

        String json = """
                {
                    "name": "Teste",
                    "price": 12.99,
                    "quantity": 5
                }
                """;

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/products")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(201, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveriaRetonar403AoCriarProdutoQuandoUsuarioNaoForAdmin() throws Exception {

        String json = """
                {
                    "name": "Teste",
                    "price": 12.99,
                    "quantity": 5
                }
                """;

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/products")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(403, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveriaRetornar409QuandoProdutoJaExistir() throws Exception {

        String json = """
                {
                    "name": "Teste",
                    "price": 12.99,
                    "quantity": 5
                }
                """;

        BDDMockito.willThrow(new AlreadyExistsException("Produto já cadastrado"))
                .given(service)
                .createProduct(BDDMockito.any(CreateProductRequest.class));

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/products")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(409, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deveriaRetornar204AoRemoverSemErro() throws Exception {

        var response = mvc.perform(
                MockMvcRequestBuilders.delete("/products/{id}", "1")
        ).andReturn().getResponse();

        Assertions.assertEquals(204, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveriaRetornar403AoRemoverQuandoUsuarioNaoForAdmin() throws Exception {

        var response = mvc.perform(
                MockMvcRequestBuilders.delete("/products/{id}", "1")
        ).andReturn().getResponse();

        Assertions.assertEquals(403, response.getStatus());
    }
}