package com.tienda.virtualstore.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "categories")
@Schema(description = "Categoría que agrupa productos del catálogo")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la categoría", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    @Schema(description = "Nombre único de la categoría", example = "Electrónica")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción opcional de la categoría",
            example = "Teléfonos, computadores y accesorios tecnológicos")
    private String description;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha de creación en UTC",
            example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}