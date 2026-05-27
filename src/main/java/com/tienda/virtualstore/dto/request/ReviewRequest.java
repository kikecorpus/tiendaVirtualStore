package com.tienda.virtualstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Datos para crear una reseña")
public class ReviewRequest {

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "ID del producto a reseñar", example = "1")
    private Long productId;

    @NotNull(message = "La calificación es obligatoria")
    @Min(value = 1, message = "La calificación mínima es 1")
    @Max(value = 5, message = "La calificación máxima es 5")
    @Schema(description = "Calificación de 1 a 5", example = "5")
    private Short rating;

    @Size(max = 1000, message = "El comentario no puede superar 1000 caracteres")
    @Schema(description = "Comentario opcional",
            example = "Excelente producto, muy buena calidad")
    private String comment;
}