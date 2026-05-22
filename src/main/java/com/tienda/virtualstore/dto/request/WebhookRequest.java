package com.tienda.virtualstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Notificación webhook de MercadoPago")
public class WebhookRequest {

    @Schema(description = "Tipo de notificación", example = "payment")
    private String type;

    @Schema(description = "Acción ejecutada", example = "payment.created")
    private String action;

    @Schema(description = "Datos de la notificación")
    private WebhookData data;

    @Data
    public static class WebhookData {
        @Schema(description = "ID del recurso", example = "123456789")
        private String id;
    }
}