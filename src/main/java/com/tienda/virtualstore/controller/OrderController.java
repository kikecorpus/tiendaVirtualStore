package com.tienda.virtualstore.controller;

import com.tienda.virtualstore.dto.request.OrderStatusRequest;
import com.tienda.virtualstore.dto.response.OrderResponse;
import com.tienda.virtualstore.security.SecurityUtils;
import com.tienda.virtualstore.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Gestión de pedidos")
@SecurityRequirement(name = "bearerAuth")
public class OrderController {

    private final OrderService  orderService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(
            summary     = "Crear pedido desde carrito",
            description = "Crea un pedido con los items del carrito activo. Descuenta stock automáticamente."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido creado",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Carrito vacío o stock insuficiente",
                    content = @Content)
    })
    public ResponseEntity<OrderResponse> createFromCart() {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(orderService.createFromCart(
                        securityUtils.getCurrentUserId()));
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Cambiar estado del pedido",
            description = "Actualiza el estado de un pedido. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "400", description = "Transición de estado inválida",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content)
    })
    public ResponseEntity<OrderResponse> updateStatus(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long orderId,
            @Valid @RequestBody OrderStatusRequest request) {

        return ResponseEntity.ok(
                orderService.updateStatus(orderId, request));
    }

    @GetMapping("/{orderId}")
    @Operation(
            summary     = "Ver detalle de un pedido",
            description = "Retorna el detalle completo de un pedido del usuario autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado",
                    content = @Content)
    })
    public ResponseEntity<OrderResponse> findById(
            @Parameter(description = "ID del pedido", example = "1")
            @PathVariable Long orderId) {

        return ResponseEntity.ok(
                orderService.findById(
                        orderId, securityUtils.getCurrentUserId()));
    }

    @GetMapping("/my-orders")
    @Operation(
            summary     = "Mi historial de compras",
            description = "Retorna todos los pedidos del usuario autenticado ordenados por fecha."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historial de pedidos",
                    content = @Content(schema = @Schema(implementation = OrderResponse.class))),
            @ApiResponse(responseCode = "401", description = "No autenticado",
                    content = @Content)
    })
    public ResponseEntity<List<OrderResponse>> findMyOrders() {
        return ResponseEntity.ok(
                orderService.findMyOrders(
                        securityUtils.getCurrentUserId()));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Listar todos los pedidos")
    @Parameters({
            @Parameter(name = "page", description = "Número de página (0..N)",
                    in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "Items por página",
                    in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "sort", description = "Campo de ordenamiento. Formato: campo,asc|desc",
                    in = ParameterIn.QUERY, example = "createdAt,desc")
    })
    public ResponseEntity<Page<OrderResponse>> findAll(
            @PageableDefault(size = 20, sort = "createdAt")
            @Parameter(hidden = true) Pageable pageable) {

        return ResponseEntity.ok(orderService.findAll(pageable));
    }


}
