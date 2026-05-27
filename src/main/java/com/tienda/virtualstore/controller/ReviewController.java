package com.tienda.virtualstore.controller;

import com.tienda.virtualstore.dto.request.ReviewRequest;
import com.tienda.virtualstore.dto.response.ReviewResponse;
import com.tienda.virtualstore.security.SecurityUtils;
import com.tienda.virtualstore.service.ReviewService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Tag(name = "Reseñas", description = "Gestión de reseñas de productos")
@SecurityRequirement(name = "bearerAuth")
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtils securityUtils;

    @PostMapping
    @Operation(
            summary     = "Crear reseña",
            description = "Crea una reseña de un producto. Solo usuarios que compraron y recibieron el producto."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Reseña creada",
                    content = @Content(schema = @Schema(implementation = ReviewResponse.class))),
            @ApiResponse(responseCode = "400", description = "Ya reseñaste este producto",
                    content = @Content),
            @ApiResponse(responseCode = "403", description = "No compraste este producto",
                    content = @Content)
    })
    public ResponseEntity<ReviewResponse> create(
            @Valid @RequestBody ReviewRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reviewService.create(
                        securityUtils.getCurrentUserId(), request));
    }

    @GetMapping("/product/{productId}")
    @Operation(
            summary     = "Ver reseñas de un producto",
            description = "Retorna todas las reseñas de un producto ordenadas por fecha."
    )
    @ApiResponse(responseCode = "200", description = "Lista de reseñas")
    public ResponseEntity<List<ReviewResponse>> findByProduct(
            @Parameter(description = "ID del producto", example = "1")
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                reviewService.findByProduct(productId));
    }

    @DeleteMapping("/{reviewId}")
    @Operation(
            summary     = "Eliminar reseña",
            description = "Elimina una reseña. Solo el autor puede eliminarla."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Reseña eliminada"),
            @ApiResponse(responseCode = "403", description = "No eres el autor de esta reseña",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Reseña no encontrada",
                    content = @Content)
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "ID de la reseña", example = "1")
            @PathVariable Long reviewId) {

        reviewService.delete(reviewId, securityUtils.getCurrentUserId());
        return ResponseEntity.noContent().build();
    }
}