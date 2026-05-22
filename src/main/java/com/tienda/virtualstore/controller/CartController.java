package com.tienda.virtualstore.controller;

import com.tienda.virtualstore.dto.request.CartItemRequest;
import com.tienda.virtualstore.dto.response.CartResponse;
import com.tienda.virtualstore.security.SecurityUtils;
import com.tienda.virtualstore.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Gestión del carrito de compras")
@SecurityRequirement(name = "bearerAuth")
public class CartController {

    private final CartService   cartService;
    private final SecurityUtils securityUtils;

    @GetMapping
    @Operation(
            summary     = "Ver mi carrito",
            description = "Retorna el carrito activo del usuario autenticado."
    )
    @ApiResponse(responseCode = "200", description = "Carrito del usuario",
            content = @Content(schema = @Schema(implementation = CartResponse.class)))
    public ResponseEntity<CartResponse> getCart() {
        return ResponseEntity.ok(
                cartService.getCart(securityUtils.getCurrentUserId()));
    }

    @PostMapping("/items")
    @Operation(
            summary     = "Agregar item al carrito",
            description = "Agrega un producto al carrito. Si ya existe suma la cantidad."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item agregado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content)
    })
    public ResponseEntity<CartResponse> addItem(
            @Valid @RequestBody CartItemRequest request) {

        return ResponseEntity.ok(
                cartService.addItem(securityUtils.getCurrentUserId(), request));
    }

    @PutMapping("/items/{itemId}")
    @Operation(
            summary     = "Actualizar cantidad de un item",
            description = "Actualiza la cantidad de un producto en el carrito."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item actualizado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Stock insuficiente",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Item no encontrado",
                    content = @Content)
    })
    public ResponseEntity<CartResponse> updateItem(
            @Parameter(description = "ID del item", example = "1")
            @PathVariable Long itemId,
            @Valid @RequestBody CartItemRequest request) {

        return ResponseEntity.ok(
                cartService.updateItem(
                        securityUtils.getCurrentUserId(), itemId, request));
    }

    @DeleteMapping("/items/{itemId}")
    @Operation(
            summary     = "Eliminar item del carrito",
            description = "Elimina un producto específico del carrito."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Item eliminado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "404", description = "Item no encontrado",
                    content = @Content)
    })
    public ResponseEntity<CartResponse> removeItem(
            @Parameter(description = "ID del item", example = "1")
            @PathVariable Long itemId) {

        return ResponseEntity.ok(
                cartService.removeItem(
                        securityUtils.getCurrentUserId(), itemId));
    }

    @DeleteMapping
    @Operation(
            summary     = "Vaciar carrito",
            description = "Elimina todos los items del carrito."
    )
    @ApiResponse(responseCode = "204", description = "Carrito vaciado")
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart(securityUtils.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}