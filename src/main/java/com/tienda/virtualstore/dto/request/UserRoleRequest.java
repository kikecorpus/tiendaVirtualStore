package com.tienda.virtualstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
@Schema(description = "Roles a asignar a un usuario")
public class UserRoleRequest {

    @NotEmpty(message = "Debe especificar al menos un rol")
    @Schema(description = "Nombres de los roles",
            example = "[\"ROLE_ADMIN\", \"ROLE_CLIENTE\"]")
    private Set<String> roles;
}