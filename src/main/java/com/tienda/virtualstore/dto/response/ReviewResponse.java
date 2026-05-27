package com.tienda.virtualstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Información de una reseña")
public class ReviewResponse {

    @Schema(description = "ID de la reseña", example = "1")
    private Long id;

    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @Schema(description = "Nombre del producto", example = "iPhone 15 Pro")
    private String productName;

    @Schema(description = "Nombre del usuario", example = "Juan Pérez")
    private String userName;

    @Schema(description = "Calificación", example = "5")
    private Short rating;

    @Schema(description = "Comentario", example = "Excelente producto")
    private String comment;

    @Schema(description = "Fecha de la reseña", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}