package com.tienda.virtual_store.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Información de una categoría")
public class CategoryResponse {

    @Schema(description = "ID único de la categoría", example = "1")
    private Long id;

    @Schema(description = "Nombre de la categoría", example = "Electrónica")
    private String name;

    @Schema(description = "Descripción de la categoría",
            example = "Teléfonos, computadores y accesorios")
    private String description;

    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}