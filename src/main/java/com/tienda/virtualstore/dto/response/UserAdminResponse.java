package com.tienda.virtualstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Schema(description = "Información completa del usuario para el panel admin")
public class UserAdminResponse {

    @Schema(description = "ID del usuario", example = "1")
    private Long id;

    @Schema(description = "Email del usuario", example = "juan@email.com")
    private String email;

    @Schema(description = "Nombre completo", example = "Juan Pérez")
    private String fullName;

    @Schema(description = "Roles asignados", example = "[\"ROLE_CLIENTE\"]")
    private Set<String> roles;

    @Schema(description = "Indica si la cuenta está activa", example = "true")
    private boolean enabled;

    @Schema(description = "Fecha de registro", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}