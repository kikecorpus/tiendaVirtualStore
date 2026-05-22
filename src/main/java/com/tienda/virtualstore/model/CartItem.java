package com.tienda.virtualstore.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "cart_items")
@Schema(description = "Item dentro del carrito de compras")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del item", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    @Schema(description = "Carrito al que pertenece el item")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Producto agregado al carrito")
    private Product product;

    @Column(nullable = false)
    @Schema(description = "Cantidad del producto", example = "2")
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Precio unitario al momento de agregar", example = "999.99")
    private BigDecimal unitPrice;
}