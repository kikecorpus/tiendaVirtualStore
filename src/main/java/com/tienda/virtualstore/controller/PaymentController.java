package com.tienda.virtualstore.controller;

import com.tienda.virtualstore.dto.request.WebhookRequest;
import com.tienda.virtualstore.dto.response.PaymentResponse;
import com.tienda.virtualstore.security.MercadoPagoWebhookValidator;
import com.tienda.virtualstore.security.SecurityUtils;
import com.tienda.virtualstore.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos con MercadoPago")
public class PaymentController {

    private final PaymentService               paymentService;
    private final SecurityUtils                securityUtils;
    private final MercadoPagoWebhookValidator  webhookValidator;

    @PostMapping("/{orderId}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary     = "Iniciar pago de un pedido",
            description = "Crea una preferencia en MercadoPago y retorna la URL del checkout."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preferencia creada exitosamente",
                    content = @Content(schema = @Schema(implementation = PaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Pedido no está en estado PENDING",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content)
    })
    public ResponseEntity<PaymentResponse> createPreference(
            @Parameter(description = "ID del pedido a pagar", example = "1")
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                paymentService.createPreference(
                        orderId,
                        securityUtils.getCurrentUserId()));
    }

    @PostMapping("/webhook")
    @Operation(
            summary     = "Webhook de MercadoPago",
            description = "Endpoint que MercadoPago llama para notificar el resultado del pago. " +
                    "Valida la firma HMAC-SHA256 del header x-signature antes de procesar."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook procesado"),
            @ApiResponse(responseCode = "401", description = "Firma del webhook inválida"),
            @ApiResponse(responseCode = "500", description = "Error procesando webhook")
    })
    public ResponseEntity<Void> webhook(
            @RequestHeader(value = "x-signature",  required = false) String xSignature,
            @RequestHeader(value = "x-request-id", required = false) String xRequestId,
            @RequestParam(value = "topic",         required = false) String topic,
            @RequestBody(required = false) WebhookRequest request,
            HttpServletRequest httpRequest) {

        // ── Descartar notificaciones de merchant_order ────────────────────────
        if ("merchant_order".equalsIgnoreCase(topic)) {
            log.info("Webhook ignorado — topic: {} (no es notificación de pago), " +
                    "requestId: {}", topic, xRequestId);
            return ResponseEntity.ok().build();
        }

        log.info("Webhook recibido — tipo: {}, acción: {}, requestId: {}",
                request != null ? request.getType()   : null,
                request != null ? request.getAction() : null,
                xRequestId);

        // ── Resolver dataId ───────────────────────────────────────────────────
        // ⚠️ Spring no puede mapear query params con punto en el nombre (data.id)
        // mediante @RequestParam, por eso usamos HttpServletRequest directamente.
        // Prioridad: ?data.id= → ?id= → body (fallback)
        String dataId = httpRequest.getParameter("data.id");
        if (dataId == null || dataId.isBlank()) {
            dataId = httpRequest.getParameter("id");
        }
        if (dataId == null || dataId.isBlank()) {
            dataId = (request != null && request.getData() != null)
                    ? request.getData().getId() : null;
        }

        log.warn("🔍 DEBUG — dataId resuelto: '{}'", dataId);

        // ── Validar firma HMAC-SHA256 ─────────────────────────────────────────
        if (!webhookValidator.isValid(dataId, xSignature, xRequestId)) {
            log.warn("Webhook rechazado — firma inválida. requestId: {}", xRequestId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        paymentService.processWebhook(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/success")
    @Operation(
            summary     = "Pago exitoso",
            description = "MercadoPago redirige aquí cuando el pago fue aprobado."
    )
    public ResponseEntity<String> success(
            @RequestParam(required = false) String preference_id,
            @RequestParam(required = false) String status) {

        log.info("Pago exitoso — preferenceId: {}, status: {}", preference_id, status);
        return ResponseEntity.ok("Pago aprobado. Puedes cerrar esta ventana.");
    }

    @GetMapping("/failure")
    @Operation(
            summary     = "Pago fallido",
            description = "MercadoPago redirige aquí cuando el pago fue rechazado."
    )
    public ResponseEntity<String> failure(
            @RequestParam(required = false) String preference_id) {

        log.info("Pago fallido — preferenceId: {}", preference_id);
        return ResponseEntity.ok("El pago fue rechazado. Intenta nuevamente.");
    }

    @GetMapping("/pending")
    @Operation(
            summary     = "Pago pendiente",
            description = "MercadoPago redirige aquí cuando el pago está pendiente de confirmación."
    )
    public ResponseEntity<String> pending(
            @RequestParam(required = false) String preference_id) {

        log.info("Pago pendiente — preferenceId: {}", preference_id);
        return ResponseEntity.ok("Tu pago está siendo procesado.");
    }
}