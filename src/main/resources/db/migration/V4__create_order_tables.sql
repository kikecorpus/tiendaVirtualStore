-- ============================================================
-- V4 - Módulo de Pedidos
-- Sprint 3 | Tablas: orders, order_items
-- ============================================================

CREATE TABLE orders (
    id         BIGSERIAL      PRIMARY KEY,
    user_id    BIGINT         NOT NULL REFERENCES users(id),
    coupon_id  BIGINT,                                          -- se agrega FK en V6 cuando exista coupons
    status     VARCHAR(20)    NOT NULL DEFAULT 'PENDING',
    subtotal   DECIMAL(10, 2) NOT NULL CHECK (subtotal >= 0),
    discount   DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (discount >= 0),
    total      DECIMAL(10, 2) NOT NULL CHECK (total >= 0),
    created_at TIMESTAMP      NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_order_status CHECK (
        status IN ('PENDING', 'PAID', 'SHIPPED', 'DELIVERED', 'CANCELLED')
    )
);

CREATE TABLE order_items (
    id          BIGSERIAL      PRIMARY KEY,
    order_id    BIGINT         NOT NULL REFERENCES orders(id) ON DELETE CASCADE,
    product_id  BIGINT         NOT NULL REFERENCES products(id),
    quantity    INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0)
);

-- Índices para historial, panel admin y reportes
CREATE INDEX idx_orders_user_id    ON orders(user_id);
CREATE INDEX idx_orders_status     ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
