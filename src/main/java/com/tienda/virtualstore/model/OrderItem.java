package com.tienda.virtualstore.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Entity
@Table(name = "order_items")
@Schema(description = "Item dentro de un pedido")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del item", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "Pedido al que pertenece el item")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Producto comprado")
    private Product product;

    @Column(nullable = false)
    @Schema(description = "Cantidad comprada", example = "2")
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Precio unitario al momento de la compra", example = "999.99")
    private BigDecimal unitPrice;
}