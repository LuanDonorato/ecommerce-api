package com.luand.ecommerce_api.service;

import com.luand.ecommerce_api.config.TokenConfig;
import com.luand.ecommerce_api.dto.Request.LoginRequest;
import com.luand.ecommerce_api.dto.Request.RegisterUserRequest;
import com.luand.ecommerce_api.dto.Response.LoginResponse;
import com.luand.ecommerce_api.dto.Response.RegisterUserResponse;
import com.luand.ecommerce_api.entity.UserEntity;
import com.luand.ecommerce_api.enums.Role;
import com.luand.ecommerce_api.exception.AlreadyExistsException;
import com.luand.ecommerce_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public RegisterUserResponse register(RegisterUserRequest request) throws AlreadyExistsException {

        if(userRepository.findUserByEmail(request.email()).isPresent()) {
            throw new AlreadyExistsException("Email já cadastrado");
        }

        UserEntity user = UserEntity.builder()
                .name(request.name())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        if(request.role() != null) {
            user.setRoles(Set.of(request.role()));
        }
        else {
            user.setRoles(Set.of(Role.ROLE_USER));
        }

        userRepository.save(user);

        return new RegisterUserResponse(user.getName(), user.getEmail());
    }

    public LoginResponse login(LoginRequest request) {

        UsernamePasswordAuthenticationToken userAndPass = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        Authentication authentication = authenticationManager.authenticate(userAndPass);
        UserEntity userEntity = (UserEntity) authentication.getPrincipal();
        String token = tokenConfig.generateToken(userEntity);
        return new LoginResponse(token);
    }
}
