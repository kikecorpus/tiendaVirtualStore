package com.tienda.virtualstore.security;

import com.tienda.virtualstore.model.Role;
import com.tienda.virtualstore.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    // ✅ MEJORA 1: Validar el secreto al arrancar la aplicación.
    // Si el secreto es menor a 32 bytes, HS256 lanza una excepción en runtime.
    // Mejor fallar rápido (fail-fast) al iniciar que en la primera petición.
    @PostConstruct
    public void validateSecret() {
        int byteLength = jwtSecret.getBytes(StandardCharsets.UTF_8).length;
        if (byteLength < 32) {
            throw new IllegalStateException(
                    "jwt.secret debe tener mínimo 32 caracteres para HS256. " +
                            "Longitud actual: " + byteLength + " bytes."
            );
        }
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getId())
                .claim("roles", user.getRoles().stream()
                        .map(Role::getName)
                        .toList())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSecretKey())
                .compact();
    }

    // ✅ MEJORA 2: extractEmail ahora absorbe la excepción y retorna null
    // si el token es inválido o está expirado. Esto evita que JwtAuthFilter
    // tenga que llamar parseClaims dos veces (una en isTokenValid y otra aquí).
    public String extractEmail(String token) {
        try {
            return parseClaims(token).getSubject();
        } catch (Exception e) {
            return null;
        }
    }

    // ✅ MEJORA 3: isTokenValid ya no es necesario en el flujo del filtro,
    // pero lo mantenemos por si se necesita en otro contexto (tests, etc.).
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Long getExpiration() {
        return jwtExpiration;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(
                jwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String generateRefreshToken() {
        return java.util.UUID.randomUUID().toString();
    }

    public Long getRefreshExpiration() {
        return refreshExpiration;
    }
}