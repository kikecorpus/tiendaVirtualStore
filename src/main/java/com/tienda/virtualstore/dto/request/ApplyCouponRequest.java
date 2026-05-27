package com.tienda.virtualstore.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Datos para aplicar un cupón al carrito")
public class ApplyCouponRequest {

    @NotBlank(message = "El código del cupón es obligatorio")
    @Schema(description = "Código del cupón", example = "VERANO20")
    private String code;
}