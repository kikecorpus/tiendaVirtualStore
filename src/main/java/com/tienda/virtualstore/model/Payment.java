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
@Table(name = "payments")
@Schema(description = "Registro de pago asociado a un pedido")
public class Payment {

    public enum Status {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID del pago", example = "1")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @Schema(description = "Pedido asociado al pago")
    private Order order;

    @Column(length = 255)
    @Schema(description = "ID de preferencia en MercadoPago",
            example = "123456789-abc-def")
    private String mpPreferenceId;

    @Column(length = 255)
    @Schema(description = "ID del pago confirmado en MercadoPago",
            example = "123456789")
    private String mpPaymentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Schema(description = "Estado del pago", example = "PENDING")
    private Status status = Status.PENDING;

    @Column(nullable = false, precision = 10, scale = 2)
    @Schema(description = "Monto procesado", example = "2799.97")
    private BigDecimal amount;

    @Schema(description = "Fecha de confirmación del pago",
            example = "2024-01-15T10:35:00")
    private LocalDateTime paidAt;

    @Column(nullable = false, updatable = false)
    @Schema(description = "Fecha de creación", example = "2024-01-15T10:30:00")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }
}