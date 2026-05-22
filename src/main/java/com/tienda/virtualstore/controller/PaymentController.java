package com.tienda.virtualstore.controller;

import com.tienda.virtualstore.dto.request.WebhookRequest;
import com.tienda.virtualstore.dto.response.PaymentResponse;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Pagos", description = "Gestión de pagos con MercadoPago")
public class PaymentController {

    private final PaymentService paymentService;
    private final SecurityUtils  securityUtils;

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
            description = "Endpoint que MercadoPago llama para notificar el resultado del pago. No requiere autenticación."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook procesado"),
            @ApiResponse(responseCode = "500", description = "Error procesando webhook")
    })
    public ResponseEntity<Void> webhook(
            @RequestBody WebhookRequest request) {

        log.info("Webhook recibido — tipo: {}, acción: {}",
                request.getType(), request.getAction());

        paymentService.processWebhook(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/success")
    @Operation(
            summary     = "Pago exitoso",
            description = "MercadoPago redirige aquí cuando el pago fue aprobado."
    )
    public ResponseEntity<String> success(
            @Parameter(description = "ID de la preferencia")
            @RequestParam(required = false) String preference_id,
            @Parameter(description = "Estado del pago")
            @RequestParam(required = false) String status) {

        log.info("Pago exitoso — preferenceId: {}, status: {}",
                preference_id, status);
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