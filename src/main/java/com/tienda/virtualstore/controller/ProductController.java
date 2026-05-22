package com.tienda.virtualstore.controller;

import com.tienda.virtualstore.dto.request.ProductRequest;
import com.tienda.virtualstore.dto.response.ProductResponse;
import com.tienda.virtualstore.dto.response.ProductSummaryResponse;
import com.tienda.virtualstore.service.ProductService;
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

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
@SecurityRequirement(name = "bearerAuth")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Crear producto",
            description = "Crea un nuevo producto en el catálogo. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content)
    })
    public ResponseEntity<ProductResponse> create(
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(productService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Actualizar producto",
            description = "Actualiza un producto existente. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto o categoría no encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content)
    })
    public ResponseEntity<ProductResponse> update(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(productService.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary     = "Buscar producto por ID",
            description = "Retorna el detalle completo de un producto activo."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado",
                    content = @Content(schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content)
    })
    public ResponseEntity<ProductResponse> findById(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping
    @Operation(
            summary     = "Listar productos",
            description = "Retorna productos activos paginados. Filtra por nombre y/o categoría."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista paginada de productos")
    })
    @Parameters({
            @Parameter(name = "page", description = "Número de página (0..N)",
                    in = ParameterIn.QUERY, example = "0"),
            @Parameter(name = "size", description = "Items por página",
                    in = ParameterIn.QUERY, example = "10"),
            @Parameter(name = "sort", description = "Campo,dirección",
                    in = ParameterIn.QUERY, example = "name,asc"),
            @Parameter(name = "name", description = "Filtrar por nombre",
                    in = ParameterIn.QUERY, example = "iPhone"),
            @Parameter(name = "categoryId", description = "Filtrar por categoría",
                    in = ParameterIn.QUERY, example = "1")
    })
    public ResponseEntity<Page<ProductSummaryResponse>> findAll(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long categoryId,
            @Parameter(hidden = true)
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return ResponseEntity.ok(
                productService.findAll(name, categoryId, pageable));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Desactivar producto",
            description = "Realiza un soft delete — el producto se desactiva pero no se elimina. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto desactivado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long id) {

        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}