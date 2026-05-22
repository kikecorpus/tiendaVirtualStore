package com.tienda.virtualstore.dto.response;

import com.tienda.virtualstore.model.Payment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Respuesta de pago con URL de checkout")
public class PaymentResponse {

    @Schema(description = "ID del pago", example = "1")
    private Long id;

    @Schema(description = "ID del pedido", example = "1")
    private Long orderId;

    @Schema(description = "URL para redirigir al cliente al checkout de MercadoPago",
            example = "https://www.mercadopago.com.co/checkout/v1/redirect?pref_id=...")
    private String checkoutUrl;

    @Schema(description = "Estado del pago", example = "PENDING")
    private Payment.Status status;

    @Schema(description = "Monto a pagar", example = "2799.97")
    private BigDecimal amount;

    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;
}