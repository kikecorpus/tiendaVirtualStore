package com.tienda.virtualstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Datos para crear o actualizar un producto")
public class ProductRequest {

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "ID de la categoría", example = "1")
    private Long categoryId;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    @Schema(description = "Nombre del producto", example = "iPhone 15 Pro")
    private String name;

    @Size(max = 1000, message = "La descripción no puede superar 1000 caracteres")
    @Schema(description = "Descripción del producto",
            example = "Smartphone Apple con chip A17 Pro")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2,
            message = "El precio debe tener máximo 8 enteros y 2 decimales")
    @Schema(description = "Precio de venta", example = "999.99")
    private BigDecimal price;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Unidades disponibles", example = "50")
    private Integer stock;

    @Size(max = 500, message = "La URL no puede superar 500 caracteres")
    @Schema(description = "URL de la imagen del producto",
            example = "https://mitienda.com/images/iphone15.jpg")
    private String imageUrl;
}