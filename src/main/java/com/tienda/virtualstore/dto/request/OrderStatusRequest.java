package com.tienda.virtualstore.dto.request;

import com.tienda.virtualstore.model.Order;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Datos para cambiar el estado de un pedido")
public class OrderStatusRequest {

    @NotNull(message = "El estado es obligatorio")
    @Schema(description = "Nuevo estado del pedido", example = "SHIPPED",
            allowableValues = {"PENDING", "PAID", "SHIPPED", "DELIVERED", "CANCELLED"})
    private Order.Status status;
}