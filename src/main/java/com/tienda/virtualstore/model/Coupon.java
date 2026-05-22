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
@Table(name = "coupons")
@Schema(description = "Cupón de descuento aplicable a pedidos")
public class Coupon {

    public enum DiscountType {
        PERCENTAGE, FIXED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del cupón", example = "1")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Schema(description = "Código único del cupón", example = "VERANO20")
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Schema(description = "Tipo de descuento", example = "PERCENTAGE")
    private DiscountType discountType;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Valor del descuento", example = "20.00")
    private BigDecimal discountValue;

    @Schema(description = "Fecha de vencimiento", example = "2024-12-31T23:59:59")
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    @Schema(description = "Indica si el cupón está activo", example = "true")
    private boolean active = true;
}