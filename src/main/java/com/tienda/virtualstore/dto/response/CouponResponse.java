package com.tienda.virtualstore.dto.response;

import com.tienda.virtualstore.model.Coupon;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Información de un cupón de descuento")
public class CouponResponse {

    @Schema(description = "ID del cupón", example = "1")
    private Long id;

    @Schema(description = "Código del cupón", example = "VERANO20")
    private String code;

    @Schema(description = "Tipo de descuento", example = "PERCENTAGE")
    private Coupon.DiscountType discountType;

    @Schema(description = "Valor del descuento", example = "20.00")
    private BigDecimal discountValue;

    @Schema(description = "Fecha de vencimiento", example = "2024-12-31T23:59:59")
    private LocalDateTime expiresAt;

    @Schema(description = "Indica si el cupón está activo", example = "true")
    private boolean active;
}