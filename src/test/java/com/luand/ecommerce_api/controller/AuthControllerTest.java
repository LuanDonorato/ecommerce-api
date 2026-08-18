package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.config.SecurityConfig;
import com.luand.ecommerce_api.config.TokenConfig;
import com.luand.ecommerce_api.dto.Request.RegisterUserRequest;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private UserService service;

    @MockitoBean
    private TokenConfig tokenConfig;

    @Test
    void deveriaDevolverCodigo201AoRegistrarUsuarioSemErro() throws Exception {

        String json = """
                {
                     "name": "Luan",
                     "email": "luand@gmail.com",
                     "password": "teste",
                     "role": "ROLE_ADMIN"
                 }
                """;

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(201, response.getStatus());
    }

    @Test
    void deveriaRetornar409QuandoUsuarioJaExistir() throws Exception {

        String json = """
                {
                     "name": "Luan",
                     "email": "luand@gmail.com",
                     "password": "teste",
                     "role": "ROLE_ADMIN"
                 }
                """;

        BDDMockito.willThrow(new AlreadyExistsException("Email já cadastrado"))
                        .given(service)
                        .register(BDDMockito.any(RegisterUserRequest.class));

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/auth/register")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(409, response.getStatus());
    }

    @Test
    void deveriaRetornar200QuandoLoginEstaCorreto() throws Exception {

        String json = """
                {
                    "email": "luandonorato@gmail.com",
                    "password": "teste"
                }
                """;

        var response = mvc.perform(
                MockMvcRequestBuilders.post("/auth/login")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andReturn().getResponse();

        Assertions.assertEquals(200, response.getStatus());
    }
}

