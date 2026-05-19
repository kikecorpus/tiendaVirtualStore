-- ============================================================
-- V2 - Módulo de Catálogo
-- Sprint 2 | Tablas: categories, products
-- ============================================================

CREATE TABLE categories (
    id          BIGSERIAL    PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE TABLE products (
    id          BIGSERIAL       PRIMARY KEY,
    category_id BIGINT          NOT NULL REFERENCES categories(id),
    name        VARCHAR(255)    NOT NULL,
    description TEXT,
    price       DECIMAL(10, 2)  NOT NULL CHECK (price >= 0),
    stock       INTEGER         NOT NULL DEFAULT 0 CHECK (stock >= 0),
    image_url   VARCHAR(500),
    active      BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP       NOT NULL DEFAULT NOW()
);

-- Índices para búsqueda y filtros
CREATE INDEX idx_products_name        ON products(name);
CREATE INDEX idx_products_category_id ON products(category_id);
CREATE INDEX idx_products_active      ON products(active);
