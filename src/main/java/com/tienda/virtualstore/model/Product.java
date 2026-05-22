package com.tienda.virtualstore.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Entity
@Table(name = "products")
@Schema(description = "Producto disponible en el catálogo de la tienda")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único del producto", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(description = "Categoría a la que pertenece el producto")
    private Category category;

    @Column(nullable = false, length = 255)
    @Schema(description = "Nombre del producto", example = "iPhone 15 Pro")
    private String name;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción detallada del producto",
            example = "Smartphone Apple con chip A17 Pro")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Precio de venta", example = "999.99")
    private BigDecimal price;

    @Column(nullable = false)
    @Schema(description = "Unidades disponibles en inventario", example = "50")
    private Integer stock;

    @Column(length = 500)
    @Schema(description = "URL de la imagen del producto",
            example = "https://mitienda.com/images/iphone15.jpg")
    private String imageUrl;

    @Column(nullable = false)
    @Schema(description = "Indica si el producto está activo en el catálogo",
            example = "true")
    private boolean active = true;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha de creación en UTC",
            example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}