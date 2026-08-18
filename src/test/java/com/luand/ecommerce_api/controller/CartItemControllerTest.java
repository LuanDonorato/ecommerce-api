package com.luand.ecommerce_api.controller;


import com.luand.ecommerce_api.config.SecurityConfig;
import com.luand.ecommerce_api.config.TokenConfig;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.service.CartItemService;
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



@WebMvcTest(CartItemController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class CartItemControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CartItemService service;

    @MockitoBean
    private TokenConfig tokenConfig;

    @Test
    @WithMockUser(roles = "USER")
    void deveriaRetornar201AoAdicionarProdutoAoCartItem() throws Exception {

        String json = """
                {
                    "productId": 1,
                    "quantity": 3
                }
                """;

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/cart/{userId}", 1)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(201, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveriaRetornar404QuandoLancaNotFoundExceptionAoAdicionarProdutoAoCartItem() throws Exception {

        String json = """
                {
                    "productId": 1,
                    "quantity": 3
                }
                """;

        BDDMockito.willThrow(new NotFoundException("Produto não encontrado"))
                .given(service)
                .addProductToCartItem(1L, 1L, 3);

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/cart/{userId}", 1L)
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveriaRetornar204AoRemoverProdutoDoCartItemSemErro() throws Exception {

        var response = mvc.perform(
                MockMvcRequestBuilders.delete("/cart/{userId}/products/{productId}", 1L, 1L)
                        .param("quantity", "3")
        ).andReturn().getResponse();


        Assertions.assertEquals(204, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveriaRetornar404QuandoLancaNotFoundExcpetionAoRemoverDoCartItem() throws Exception {

        BDDMockito.willThrow(new NotFoundException("Carrinho não encontrado"))
                .given(service)
                .removeProductOfCartItem(1L, 1L, 3);

        var response = mvc.perform(
                MockMvcRequestBuilders.delete("/cart/{userId}/products/{productId}", 1L, 1L)
                        .param("quantity", "3")
        ).andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }

}