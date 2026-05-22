package com.tienda.virtualstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos para agregar o actualizar un item en el carrito")
public class CartItemRequest {

    @NotNull(message = "El producto es obligatorio")
    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad mínima es 1")
    @Schema(description = "Cantidad del producto", example = "2")
    private Integer quantity;
}