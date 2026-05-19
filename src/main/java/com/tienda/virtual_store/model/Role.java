package com.tienda.virtual_store.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "roles")
@Schema(description = "Rol de acceso asignado a un usuario del sistema")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del rol", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "Nombre del rol", example = "ROLE_ADMIN",
            allowableValues = {"ROLE_ADMIN", "ROLE_CLIENTE"})
    private String name;
}