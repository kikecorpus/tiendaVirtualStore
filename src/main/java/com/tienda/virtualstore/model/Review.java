package com.tienda.virtualstore.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "reviews",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"user_id", "product_id"}
        ))
@Schema(description = "Reseña de un producto por un usuario")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID de la reseña", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Usuario que escribió la reseña")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Producto reseñado")
    private Product product;

    @Column(nullable = false)
    @Schema(description = "Calificación de 1 a 5", example = "5")
    private Short rating;  // ← Short mapea a SMALLINT en PostgreSQL

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Comentario opcional", example = "Excelente producto")
    private String comment;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha de la reseña", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}