package com.tienda.virtualstore.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "orders")
@Schema(description = "Pedido realizado por un usuario")
public class Order {

    public enum Status {
        PENDING, PAID, SHIPPED, DELIVERED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del pedido", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "Usuario que realizó el pedido")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    @Schema(description = "Cupón aplicado al pedido")
    private Coupon coupon;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "Estado del pedido", example = "PENDING")
    private Status status = Status.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Suma de todos los items", example = "1999.98")
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Monto descontado por cupón", example = "0.00")
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Total final a cobrar", example = "1999.98")
    private BigDecimal total;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Schema(description = "Items del pedido")
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}