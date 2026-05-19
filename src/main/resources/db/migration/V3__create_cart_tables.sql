-- ============================================================
-- V3 - Módulo de Carrito
-- Sprint 3 | Tablas: carts, cart_items
-- ============================================================

CREATE TABLE carts (
    id         BIGSERIAL PRIMARY KEY,
    user_id    BIGINT    NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE cart_items (
    id          BIGSERIAL      PRIMARY KEY,
    cart_id     BIGINT         NOT NULL REFERENCES carts(id) ON DELETE CASCADE,
    product_id  BIGINT         NOT NULL REFERENCES products(id),
    quantity    INTEGER        NOT NULL CHECK (quantity > 0),
    unit_price  DECIMAL(10, 2) NOT NULL CHECK (unit_price >= 0),
    UNIQUE (cart_id, product_id)
);

-- Índice para consultar items de un carrito
CREATE INDEX idx_cart_items_cart_id ON cart_items(cart_id);
