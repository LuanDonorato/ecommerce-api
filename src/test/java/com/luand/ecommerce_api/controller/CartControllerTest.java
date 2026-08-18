package com.luand.ecommerce_api.controller;


import com.luand.ecommerce_api.config.SecurityConfig;
import com.luand.ecommerce_api.config.TokenConfig;
import com.luand.ecommerce_api.exception.NotFoundException;
import com.luand.ecommerce_api.service.CartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(CartController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class CartControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CartService service;

    @MockitoBean
    private TokenConfig tokenConfig;

    @Test
    @WithMockUser(roles = "USER")
    void deveriaRetornar200AoChamarGetCart() throws Exception {

        var response = mvc.perform(
                MockMvcRequestBuilders.get("/carts/{userId}", 1)
        ).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    @WithMockUser(roles = "USER")
    void deveriaRetornar404QuandoLancaNotFoundExceptionAoChamarGetCart() throws Exception {

        BDDMockito.willThrow(new NotFoundException("Carrinho não encontrado"))
                .given(service)
                .getCart(1L);

        var response = mvc.perform(
                MockMvcRequestBuilders.get("/carts/{userId}", 1)
        ).andReturn().getResponse();

        Assertions.assertEquals(404, response.getStatus());
    }

}