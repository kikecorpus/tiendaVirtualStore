package com.tienda.virtualstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Carrito de compras del usuario")
public class CartResponse {

    @Schema(description = "ID del carrito", example = "1")
    private Long id;

    @Schema(description = "Items en el carrito")
    private List<CartItemResponse> items;

    @Schema(description = "Total calculado del carrito", example = "1999.98")
    private BigDecimal total;

    @Schema(description = "Cantidad de items en el carrito", example = "2")
    private Integer itemCount;
}