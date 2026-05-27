package com.tienda.virtualstore.controller;

import com.tienda.virtualstore.dto.request.ApplyCouponRequest;
import com.tienda.virtualstore.dto.request.CouponRequest;
import com.tienda.virtualstore.dto.response.CartResponse;
import com.tienda.virtualstore.dto.response.CouponResponse;
import com.tienda.virtualstore.security.SecurityUtils;
import com.tienda.virtualstore.service.CouponService;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coupons")
@RequiredArgsConstructor
@Tag(name = "Cupones", description = "Gestión de cupones de descuento")
@SecurityRequirement(name = "bearerAuth")
public class CouponController {

    private final CouponService couponService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Crear cupón",
            description = "Crea un nuevo cupón de descuento. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cupón creado",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "400", description = "Código duplicado o datos inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content)
    })
    public ResponseEntity<CouponResponse> create(
            @Valid @RequestBody CouponRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(couponService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Actualizar cupón",
            description = "Actualiza un cupón existente. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cupón actualizado",
                    content = @Content(schema = @Schema(implementation = CouponResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cupón no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content)
    })
    public ResponseEntity<CouponResponse> update(
            @Parameter(description = "ID del cupón", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CouponRequest request) {

        return ResponseEntity.ok(couponService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Eliminar cupón",
            description = "Elimina un cupón por ID. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cupón eliminado"),
            @ApiResponse(responseCode = "404", description = "Cupón no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del cupón", example = "1")
            @PathVariable Long id) {

        couponService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Listar cupones",
            description = "Retorna todos los cupones. Solo ADMIN."
    )
    @ApiResponse(responseCode = "200", description = "Lista de cupones")
    public ResponseEntity<List<CouponResponse>> findAll() {
        return ResponseEntity.ok(couponService.findAll());
    }

    @PostMapping("/apply")
    @Operation(
            summary     = "Aplicar cupón al carrito",
            description = "Aplica un cupón de descuento al carrito activo del usuario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cupón aplicado",
                    content = @Content(schema = @Schema(implementation = CartResponse.class))),
            @ApiResponse(responseCode = "400", description = "Cupón inválido, vencido o carrito vacío",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Cupón no encontrado",
                    content = @Content)
    })
    public ResponseEntity<CartResponse> applyCoupon(
            @Valid @RequestBody ApplyCouponRequest request) {

        return ResponseEntity.ok(
                couponService.applyCoupon(
                        securityUtils.getCurrentUserId(), request));
    }

    @DeleteMapping("/apply")
    @Operation(
            summary     = "Remover cupón del carrito",
            description = "Elimina el cupón aplicado al carrito activo."
    )
    @ApiResponse(responseCode = "200", description = "Cupón removido",
            content = @Content(schema = @Schema(implementation = CartResponse.class)))
    public ResponseEntity<CartResponse> removeCoupon() {

        return ResponseEntity.ok(
                couponService.removeCoupon(
                        securityUtils.getCurrentUserId()));
    }
}