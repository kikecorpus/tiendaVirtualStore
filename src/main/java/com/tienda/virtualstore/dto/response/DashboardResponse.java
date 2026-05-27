package com.tienda.virtualstore.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "Estadísticas del panel de administración")
public class DashboardResponse {

    @Schema(description = "Total de pedidos", example = "150")
    private Long totalOrders;

    @Schema(description = "Pedidos pendientes", example = "20")
    private Long pendingOrders;

    @Schema(description = "Pedidos pagados", example = "80")
    private Long paidOrders;

    @Schema(description = "Pedidos enviados", example = "30")
    private Long shippedOrders;

    @Schema(description = "Pedidos entregados", example = "15")
    private Long deliveredOrders;

    @Schema(description = "Pedidos cancelados", example = "5")
    private Long cancelledOrders;

    @Schema(description = "Total de ingresos", example = "150000.00")
    private BigDecimal totalRevenue;

    @Schema(description = "Total de usuarios registrados", example = "200")
    private Long totalUsers;

    @Schema(description = "Total de productos activos", example = "50")
    private Long totalProducts;

    @Schema(description = "Productos con stock bajo", example = "5")
    private Long lowStockProducts;

    @Schema(description = "Últimos 5 pedidos")
    private List<OrderResponse> recentOrders;
}