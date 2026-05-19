package com.tienda.virtual_store.controller;

import com.tienda.virtual_store.dto.request.CategoryRequest;
import com.tienda.virtual_store.dto.response.CategoryResponse;
import com.tienda.virtual_store.service.CategoryService;
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
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "Categorías", description = "Gestión del catálogo de categorías")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Crear categoría",
            description = "Crea una nueva categoría. Solo accesible para ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoría creada",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o nombre duplicado",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content)
    })
    public ResponseEntity<CategoryResponse> create(
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(categoryService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Actualizar categoría",
            description = "Actualiza nombre y descripción de una categoría existente. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría actualizada",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content)
    })
    public ResponseEntity<CategoryResponse> update(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(categoryService.update(id, request));
    }

    @GetMapping("/{id}")
    @Operation(
            summary     = "Buscar categoría por ID",
            description = "Retorna una categoría específica. Accesible para todos los usuarios autenticados."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoría encontrada",
                    content = @Content(schema = @Schema(implementation = CategoryResponse.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content)
    })
    public ResponseEntity<CategoryResponse> findById(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id) {

        return ResponseEntity.ok(categoryService.findById(id));
    }

    @GetMapping
    @Operation(
            summary     = "Listar todas las categorías",
            description = "Retorna la lista completa de categorías disponibles."
    )
    @ApiResponse(responseCode = "200", description = "Lista de categorías",
            content = @Content(schema = @Schema(implementation = CategoryResponse.class)))
    public ResponseEntity<List<CategoryResponse>> findAll() {
        return ResponseEntity.ok(categoryService.findAll());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary     = "Eliminar categoría",
            description = "Elimina una categoría por ID. Solo ADMIN."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoría eliminada"),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "Sin permisos de ADMIN",
                    content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la categoría", example = "1")
            @PathVariable Long id) {

        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}