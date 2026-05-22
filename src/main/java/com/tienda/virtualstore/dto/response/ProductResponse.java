package com.tienda.virtualstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Información completa de un producto")
public class ProductResponse {

    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Categoría del producto")
    private CategoryResponse category;

    @Schema(description = "Nombre del producto", example = "iPhone 15 Pro")
    private String name;

    @Schema(description = "Descripción del producto",
            example = "Smartphone Apple con chip A17 Pro")
    private String description;

    @Schema(description = "Precio de venta", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Unidades en inventario", example = "50")
    private Integer stock;

    @Schema(description = "URL de la imagen",
            example = "https://mitienda.com/images/iphone15.jpg")
    private String imageUrl;

    @Schema(description = "Indica si está activo en el catálogo", example = "true")
    private boolean active;

    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}