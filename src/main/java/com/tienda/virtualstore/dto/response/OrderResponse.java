package com.tienda.virtualstore.dto.response;

import com.tienda.virtualstore.model.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "Pedido realizado por el usuario")
public class OrderResponse {

    @Schema(description = "ID del pedido", example = "1")
    private Long id;

    @Schema(description = "Estado del pedido", example = "PENDING")
    private Order.Status status;

    @Schema(description = "Items del pedido")
    private List<OrderItemResponse> items;

    @Schema(description = "Subtotal antes del descuento", example = "1999.98")
    private BigDecimal subtotal;

    @Schema(description = "Descuento aplicado", example = "0.00")
    private BigDecimal discount;

    @Schema(description = "Total final", example = "1999.98")
    private BigDecimal total;

    @Schema(description = "Código del cupón usado", example = "VERANO20")
    private String couponCode;

    @Schema(description = "Fecha del pedido", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}