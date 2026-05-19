-- ============================================================
-- V6 - Módulo de Cupones
-- Sprint 4 | Tabla: coupons + FK en orders
-- ============================================================

CREATE TABLE coupons (
    id              BIGSERIAL      PRIMARY KEY,
    code            VARCHAR(50)    NOT NULL UNIQUE,
    discount_type   VARCHAR(20)    NOT NULL,
    discount_value  DECIMAL(10, 2) NOT NULL CHECK (discount_value > 0),
    expires_at      TIMESTAMP,
    active          BOOLEAN        NOT NULL DEFAULT TRUE,

    CONSTRAINT chk_coupon_discount_type CHECK (
        discount_type IN ('PERCENTAGE', 'FIXED')
    )
);

-- Ahora que existe la tabla coupons, se agrega la FK en orders
ALTER TABLE orders
    ADD CONSTRAINT fk_orders_coupon_id
    FOREIGN KEY (coupon_id) REFERENCES coupons(id);

-- Índice para buscar cupón por código (endpoint de validación)
CREATE INDEX idx_coupons_code   ON coupons(code);
CREATE INDEX idx_coupons_active ON coupons(active);
