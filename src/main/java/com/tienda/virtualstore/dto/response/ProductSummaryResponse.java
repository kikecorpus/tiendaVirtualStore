package com.tienda.virtualstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Resumen de producto para listados y búsquedas")
public class ProductSummaryResponse {

    @Schema(description = "ID del producto", example = "1")
    private Long id;

    @Schema(description = "Nombre del producto", example = "iPhone 15 Pro")
    private String name;

    @Schema(description = "Precio de venta", example = "999.99")
    private BigDecimal price;

    @Schema(description = "Unidades disponibles", example = "50")
    private Integer stock;

    @Schema(description = "URL de la imagen",
            example = "https://mitienda.com/images/iphone15.jpg")
    private String imageUrl;

    @Schema(description = "Nombre de la categoría", example = "Electrónica")
    private String categoryName;
}