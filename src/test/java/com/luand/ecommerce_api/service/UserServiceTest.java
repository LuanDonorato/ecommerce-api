package com.luand.ecommerce_api.service;

import com.luand.ecommerce_api.config.TokenConfig;
import com.luand.ecommerce_api.dto.Request.LoginRequest;
import com.luand.ecommerce_api.dto.Request.RegisterUserRequest;
import com.luand.ecommerce_api.entity.UserEntity;
import com.luand.ecommerce_api.enums.Role;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private TokenConfig tokenConfig;

    @Captor
    private ArgumentCaptor<UserEntity> userCaptor;

    @Captor
    private ArgumentCaptor<UsernamePasswordAuthenticationToken> tokenCaptor;

    @Test
    void deveriaRegistrarUsuarioQuandoRoleNaoInformada() throws AlreadyExistsException {

        RegisterUserRequest request = new RegisterUserRequest(
                "Teste",
                "EmailTeste@gmail.com",
                "Teste123",
                null);

        BDDMockito.given(passwordEncoder.encode(request.password())).willReturn("senha criptografada");
        BDDMockito.given(userRepository.findUserByEmail(request.email())).willReturn(Optional.empty());

        userService.register(request);

        BDDMockito.then(userRepository).should().save(userCaptor.capture());
        UserEntity userSaved = userCaptor.getValue();
        Assertions.assertEquals(Set.of(Role.ROLE_USER), userSaved.getRoles());
        Assertions.assertEquals("senha criptografada", userSaved.getPassword());
        Assertions.assertEquals(request.name(), userSaved.getName());
        Assertions.assertEquals(request.email(), userSaved.getEmail());
    }

    @Test
    void deveriaRegistrarUsuarioComRoleAdmin() throws AlreadyExistsException {

        RegisterUserRequest request = new RegisterUserRequest(
                "Teste",
                "EmailTeste@gmail.com",
                "Teste123",
                Role.ROLE_ADMIN);

        BDDMockito.given(userRepository.findUserByEmail(request.email())).willReturn(Optional.empty());

        userService.register(request);

        BDDMockito.then(userRepository).should().save(userCaptor.capture());
        UserEntity userSaved = userCaptor.getValue();
        Assertions.assertEquals(Set.of(Role.ROLE_ADMIN), userSaved.getRoles());
    }

    @Test
    void deveriaLancarAlreadyExistsException() {

        RegisterUserRequest request = new RegisterUserRequest(
                "Teste",
                "EmailTeste@gmail.com",
                "Teste123",
                Role.ROLE_ADMIN);

        BDDMockito.given(userRepository.findUserByEmail(request.email())).willReturn(Optional.of(new UserEntity()));

        AlreadyExistsException exception = Assertions.assertThrows(
                AlreadyExistsException.class, () -> userService.register(request));

        Assertions.assertEquals("Email já cadastrado", exception.getMessage());
        BDDMockito.then(userRepository).should(BDDMockito.never()).save(BDDMockito.any());
    }

    @Test
    void deveriaFazerLoginValido() {

        LoginRequest request = new LoginRequest("EmailTeste@gmail.com", "Senha teste");
        UserEntity user = UserEntity.builder().id(1L).email("EmailTeste@gmail.com").build();

        BDDMockito.given(authentication.getPrincipal()).willReturn(user);
        BDDMockito.given(authenticationManager.authenticate(BDDMockito.any())).willReturn(authentication);
        BDDMockito.given(tokenConfig.generateToken(user)).willReturn("Teste token");

        userService.login(request);

        BDDMockito.then(authenticationManager).should().authenticate(tokenCaptor.capture());
        UsernamePasswordAuthenticationToken tokenSaved = tokenCaptor.getValue();
        Assertions.assertEquals(request.email(), tokenSaved.getName());
        Assertions.assertEquals(request.password(), tokenSaved.getCredentials());
    }
}