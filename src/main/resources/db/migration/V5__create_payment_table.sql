-- ============================================================
-- V5 - Módulo de Pagos
-- Sprint 4 | Tabla: payments
-- ============================================================

CREATE TABLE payments (
    id                BIGSERIAL      PRIMARY KEY,
    order_id          BIGINT         NOT NULL REFERENCES orders(id),
    mp_preference_id  VARCHAR(255),
    mp_payment_id     VARCHAR(255),
    status            VARCHAR(50)    NOT NULL DEFAULT 'PENDING',
    amount            DECIMAL(10, 2) NOT NULL CHECK (amount >= 0),
    paid_at           TIMESTAMP,

    CONSTRAINT chk_payment_status CHECK (
        status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')
    )
);

-- Índice para buscar pago por order_id y por mp_payment_id (webhook idempotente)
CREATE INDEX idx_payments_order_id      ON payments(order_id);
CREATE INDEX idx_payments_mp_payment_id ON payments(mp_payment_id);
