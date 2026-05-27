package com.tienda.virtualstore.controller;

import com.tienda.virtualstore.dto.request.UserRoleRequest;
import com.tienda.virtualstore.dto.response.DashboardResponse;
import com.tienda.virtualstore.dto.response.ProductResponse;
import com.tienda.virtualstore.dto.response.UserAdminResponse;
import com.tienda.virtualstore.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameters;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Panel de administración — Solo ADMIN")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    @Operation(
            summary     = "Listar usuarios",
            description = "Retorna todos los usuarios paginados."
    )
    @Parameters({
            @Parameter(name = "page", in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "sort", in = ParameterIn.QUERY, example = "createdAt,desc")
    })
    @ApiResponse(responseCode = "200", description = "Lista de usuarios")
    public ResponseEntity<Page<UserAdminResponse>> findAllUsers(
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "createdAt")
            Pageable pageable) {

        return ResponseEntity.ok(adminService.findAllUsers(pageable));
    }

    @GetMapping("/users/{id}")
    @Operation(
            summary     = "Buscar usuario por ID",
            description = "Retorna el detalle completo de un usuario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Usuario encontrado",
                    content = @Content(schema = @Schema(implementation = UserAdminResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content)
    })
    public ResponseEntity<UserAdminResponse> findUserById(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(adminService.findUserById(id));
    }

    @PatchMapping("/users/{id}/roles")
    @Operation(
            summary     = "Actualizar roles de un usuario",
            description = "Reemplaza los roles de un usuario. Envía los roles completos."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Roles actualizados",
                    content = @Content(schema = @Schema(implementation = UserAdminResponse.class))),
            @ApiResponse(responseCode = "404", description = "Usuario o rol no encontrado",
                    content = @Content)
    })
    public ResponseEntity<UserAdminResponse> updateUserRoles(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody UserRoleRequest request) {

        return ResponseEntity.ok(adminService.updateUserRoles(id, request));
    }

    @PatchMapping("/users/{id}/toggle-status")
    @Operation(
            summary     = "Activar o desactivar usuario",
            description = "Cambia el estado enabled del usuario."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estado actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
                    content = @Content)
    })
    public ResponseEntity<Void> toggleUserStatus(
            @Parameter(description = "ID del usuario", example = "1")
            @PathVariable Long id) {

        adminService.toggleUserStatus(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/dashboard")
    @Operation(
            summary     = "Dashboard de administración",
            description = "Retorna estadísticas generales de la tienda."
    )
    @ApiResponse(responseCode = "200", description = "Estadísticas del dashboard",
            content = @Content(schema = @Schema(implementation = DashboardResponse.class)))
    public ResponseEntity<DashboardResponse> getDashboard() {
        return ResponseEntity.ok(adminService.getDashboard());
    }

    @GetMapping("/products/low-stock")
    @Operation(
            summary     = "Productos con stock bajo",
            description = "Retorna productos activos con stock menor o igual a 5. Solo ADMIN."
    )
    @ApiResponse(responseCode = "200", description = "Lista de productos con stock bajo")
    public ResponseEntity<List<ProductResponse>> getLowStockProducts() {
        return ResponseEntity.ok(adminService.getLowStockProducts());
    }
}