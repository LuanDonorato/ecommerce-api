package com.luand.ecommerce_api.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.luand.ecommerce_api.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Optional;


@Component
public class TokenConfig {

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Value("${jwt.key}")
    private String key;

    private Algorithm getSigningKey() {
        return Algorithm.HMAC256(key);
    }

    public String generateToken(UserEntity user) {
        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + expirationTime);

        return JWT.create()
                .withClaim("userId", user.getId())
                .withClaim("roles", user.getRoles().stream().map(Enum::name).toList())
                .withSubject(user.getEmail())
                .withIssuedAt(now)
                .withExpiresAt(expirationDate)
                .sign(getSigningKey());
    }

    public Optional<JWTUserData> validateToken(String token) {

        try {
            DecodedJWT decode = JWT.require(getSigningKey())
                    .build().verify(token);

            return Optional.of(JWTUserData.builder()
                    .userId(decode.getClaim("userId").asLong())
                    .email(decode.getSubject())
                    .roles(decode.getClaim("roles").asList(String.class))
                    .build());
        }catch (JWTVerificationException e) {
            return Optional.empty();
        }
    }
}
