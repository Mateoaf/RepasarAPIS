package com.example.refrescar_apis.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {
    // 1. Inyectamos la clave secreta desde application.properties
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Crea un nuevo token JWT para el usuario.
     */
    public String createToken(UserDetails userDetails) {
        // Usamos la clave secreta para firmar el token
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        return JWT.create()
                .withIssuer("refrescar-api") // El "emisor"
                .withSubject(userDetails.getUsername()) // El "dueño" (el username)
                .withIssuedAt(new Date()) // Fecha de creación
                .withExpiresAt(new Date(System.currentTimeMillis() + (1000 * 60 * 60))) // Caduca en 1 hora
                .sign(algorithm);
    }

    /**
     * Valida un token y, si es válido, devuelve el username.
     * Si no es válido (firma incorrecta, caducado...), lanza una excepción.
     */
    public String validateTokenAndGetUser(String token) throws JWTVerificationException {
        Algorithm algorithm = Algorithm.HMAC256(secretKey);
        JWTVerifier verifier = JWT.require(algorithm)
                .withIssuer("refrescar-api")
                .build();

        // Si la verificación falla (firma, caducidad, etc.),
        // el método .verify() lanzará una JWTVerificationException
        DecodedJWT decodedJwt = verifier.verify(token);

        return decodedJwt.getSubject(); // Devuelve el username
    }
}
