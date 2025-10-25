package com.victor.TCC.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.victor.TCC.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;

@Component
public class TokenConfig {

    private String secret = "secret";

    public String generateToken(User user) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.create()
                    .withClaim("userId", user.getId())
                    .withClaim("roles", user.getRoles().stream().map(Enum::name).toList())
                    .withIssuer("api-auth")
                    .withSubject(user.getEmail())
                    .withExpiresAt(Instant.now().plusSeconds(86400))
                    .withIssuedAt(Instant.now())
                    .sign(algorithm);
        } catch (JWTCreationException e) {
            throw new RuntimeException("Error creating JWT", e);
        }


    }

    public Optional<JWTUserData> validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            DecodedJWT decodedJWT = JWT.require(algorithm)
                    .build().verify(token);

            return Optional.of(JWTUserData.builder()
                    .userId(decodedJWT.getClaim("userId").asLong())
                    .email(decodedJWT.getSubject())
                    .roles(decodedJWT.getClaim("roles").asList(String.class))
                    .build()
            );

        } catch (JWTVerificationException e) {
            throw new RuntimeException("Error validating token", e);
        }
    }
}
