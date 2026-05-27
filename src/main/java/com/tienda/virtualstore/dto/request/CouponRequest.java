package com.tienda.virtualstore.dto.request;

import com.tienda.virtualstore.model.Coupon;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Schema(description = "Datos para crear un cupón de descuento")
public class CouponRequest {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede superar 50 caracteres")
    @Schema(description = "Código único del cupón", example = "VERANO20")
    private String code;

    @NotNull(message = "El tipo de descuento es obligatorio")
    @Schema(description = "Tipo de descuento", example = "PERCENTAGE",
            allowableValues = {"PERCENTAGE", "FIXED"})
    private Coupon.DiscountType discountType;

    @NotNull(message = "El valor del descuento es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false,
            message = "El descuento debe ser mayor a 0")
    @Schema(description = "Valor del descuento. Si es PERCENTAGE: 1-100. Si es FIXED: monto.",
            example = "20.00")
    private BigDecimal discountValue;

    @Schema(description = "Fecha de vencimiento. Null = sin vencimiento",
            example = "2024-12-31T23:59:59")
    private LocalDateTime expiresAt;
}