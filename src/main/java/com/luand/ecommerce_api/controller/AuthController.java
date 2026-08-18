package com.luand.ecommerce_api.controller;

import com.luand.ecommerce_api.dto.Request.LoginRequest;
import com.luand.ecommerce_api.dto.Request.RegisterUserRequest;
import com.luand.ecommerce_api.dto.Response.LoginResponse;
import com.luand.ecommerce_api.dto.Response.RegisterUserResponse;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterUserResponse register(@Valid @RequestBody RegisterUserRequest request) throws AlreadyExistsException {
        return userService.register(request);
    }

    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public LoginResponse login (@Valid @RequestBody LoginRequest request) {
        return userService.login(request);
    }
}
