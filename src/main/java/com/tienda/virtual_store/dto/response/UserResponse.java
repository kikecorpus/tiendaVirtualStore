package com.tienda.virtual_store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "Información pública del usuario")
public class UserResponse {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Email del usuario", example = "juan@email.com")
    private String email;

    @Schema(description = "Nombre del usuario", example = "Juan")
    private String firstName;

    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Schema(description = "Indica si la cuenta está activa", example = "true")
    private boolean enabled;

    @Schema(description = "Roles asignados al usuario",
            example = "[\"ROLE_CLIENTE\"]")
    private Set<String> roles;

    @Schema(description = "Fecha de registro", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}