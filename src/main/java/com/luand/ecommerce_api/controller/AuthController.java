package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.dto.Request.LoginRequest;
import com.luand.ecommerce_api.dto.Request.RegisterUserRequest;
import com.luand.ecommerce_api.dto.Response.LoginResponse;
import com.luand.ecommerce_api.dto.Response.RegisterUserResponse;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação", description = "Oporeções de autenticação do usuário")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Registrar um usuário")
    public RegisterUserResponse register(@Valid @RequestBody RegisterUserRequest request) throws AlreadyExistsException {
        return userService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Fazer login de usuário")
    public LoginResponse login (@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
