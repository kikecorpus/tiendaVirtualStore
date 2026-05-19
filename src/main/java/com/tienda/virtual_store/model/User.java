package com.tienda.virtual_store.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
@Schema(description = "Usuario registrado en la tienda virtual")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del usuario", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    @Schema(description = "Email del usuario, usado como username",
            example = "juan@email.com")
    private String email;

    @Column(nullable = false, length = 255)
    @Schema(description = "Hash BCrypt de la contraseña. Nunca texto plano",
            accessMode = Schema.AccessMode.WRITE_ONLY)
    private String passwordHash;

    @Column(nullable = false, length = 100)
    @Schema(description = "Nombre del usuario", example = "Juan")
    private String firstName;

    @Column(nullable = false, length = 100)
    @Schema(description = "Apellido del usuario", example = "Pérez")
    private String lastName;

    @Column(nullable = false)
    @Schema(description = "Indica si la cuenta está activa", example = "true")
    private boolean enabled = true;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha y hora de registro en UTC",
            example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns        = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @Schema(description = "Roles asignados al usuario")
    private Set<Role> roles = new HashSet<>();

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}