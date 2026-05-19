-- ============================================================
-- V7 - Módulo de Reseñas
-- Sprint 5 | Tabla: reviews
-- ============================================================

CREATE TABLE reviews (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT    NOT NULL REFERENCES users(id),
    product_id  BIGINT    NOT NULL REFERENCES products(id),
    rating      SMALLINT  NOT NULL,
    comment     TEXT,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW(),

    -- Un usuario solo puede reseñar un producto una vez
    UNIQUE (user_id, product_id),

    CONSTRAINT chk_review_rating CHECK (rating BETWEEN 1 AND 5)
);

-- Índice para consultar reseñas de un producto
CREATE INDEX idx_reviews_product_id ON reviews(product_id);
CREATE INDEX idx_reviews_created_at ON reviews(created_at);
