package com.tienda.virtualstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "Item dentro de un pedido")
public class OrderItemResponse {

    @Schema(description = "ID del item", example = "1")
    private Long id;

    @Schema(description = "ID del producto", example = "1")
    private Long productId;

    @Schema(description = "Nombre del producto", example = "iPhone 15 Pro")
    private String productName;

    @Schema(description = "Cantidad comprada", example = "2")
    private Integer quantity;

    @Schema(description = "Precio unitario al momento de la compra", example = "999.99")
    private BigDecimal unitPrice;

    @Schema(description = "Subtotal del item", example = "1999.98")
    private BigDecimal subtotal;
}