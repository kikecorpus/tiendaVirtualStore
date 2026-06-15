package com.tienda.virtualstore.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Respuesta de error estandarizada")
public class ErrorResponse {

    @Schema(description = "Código HTTP", example = "404")
    private int status;

    @Schema(description = "Tipo de error", example = "NOT_FOUND")
    private String error;

    @Schema(description = "Mensaje descriptivo", example = "Producto no encontrado con id: 1")
    private String message;

    @Schema(description = "Ruta que generó el error", example = "/api/products/1")
    private String path;

    @Schema(description = "Fecha y hora del error")
    private LocalDateTime timestamp;

    @Schema(description = "Lista de errores de validación")
    private List<ValidationError> validationErrors;

    @Data
    @Builder
    public static class ValidationError {
        private String field;
        private String message;
    }
}