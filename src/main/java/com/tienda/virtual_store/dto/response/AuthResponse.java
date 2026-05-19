package com.tienda.virtual_store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Respuesta de autenticación con token JWT")
public class AuthResponse {

    @Schema(description = "Token JWT para autenticar requests",
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "Tipo de token", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "Tiempo de expiración en milisegundos", example = "86400000")
    private Long expiresIn;

    @Schema(description = "Información básica del usuario autenticado")
    private UserResponse user;

    public AuthResponse(String token, Long expiresIn, UserResponse user) {
        this.token     = token;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user      = user;
    }
}