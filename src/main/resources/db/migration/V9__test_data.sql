-- ============================================================
-- V9 - Datos de prueba completos
-- Cubre TODAS las tablas en orden de dependencias:
--   roles → users → user_roles → categories → products
--   → carts → cart_items → coupons → orders → order_items
--   → payments → reviews
-- ============================================================

-- ============================================================
-- ROLES (ya existen ROLE_ADMIN y ROLE_CLIENTE desde V1/V8)
-- Se insertan de forma segura por si se ejecuta en BD limpia
-- ============================================================
INSERT INTO roles (name)
VALUES ('ROLE_ADMIN'), ('ROLE_CLIENTE')
ON CONFLICT (name) DO NOTHING;


-- ============================================================
-- USERS  (contraseña de todos: Test1234!)
-- Hash BCrypt rounds=12 de "Test1234!"
-- ============================================================
INSERT INTO users (email, password_hash, first_name, last_name, enabled) VALUES
('admin@tienda.com',   '$2b$12$bvk7OgpsLQhpfZI8MRx.ve1M1rZJF2ecYvI0t3d5hp13QqQ6wlg5y', 'Admin',    'Tienda',    TRUE),
('ana.garcia@mail.com','$2b$12$bvk7OgpsLQhpfZI8MRx.ve1M1rZJF2ecYvI0t3d5hp13QqQ6wlg5y', 'Ana',      'García',    TRUE),
('carlos.m@mail.com',  '$2b$12$bvk7OgpsLQhpfZI8MRx.ve1M1rZJF2ecYvI0t3d5hp13QqQ6wlg5y', 'Carlos',   'Martínez',  TRUE),
('lucia.r@mail.com',   '$2b$12$bvk7OgpsLQhpfZI8MRx.ve1M1rZJF2ecYvI0t3d5hp13QqQ6wlg5y', 'Lucía',    'Rodríguez', TRUE),
('pedro.h@mail.com',   '$2b$12$bvk7OgpsLQhpfZI8MRx.ve1M1rZJF2ecYvI0t3d5hp13QqQ6wlg5y', 'Pedro',    'Herrera',   TRUE),
('sofia.v@mail.com',   '$2b$12$bvk7OgpsLQhpfZI8MRx.ve1M1rZJF2ecYvI0t3d5hp13QqQ6wlg5y', 'Sofía',    'Vargas',    FALSE)  -- usuario deshabilitado (para probar enabled=false)
ON CONFLICT (email) DO NOTHING;


-- ============================================================
-- USER_ROLES
-- ============================================================
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@tienda.com' AND r.name = 'ROLE_ADMIN'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email IN ('ana.garcia@mail.com','carlos.m@mail.com','lucia.r@mail.com',
                  'pedro.h@mail.com','sofia.v@mail.com')
  AND r.name = 'ROLE_CLIENTE'
ON CONFLICT DO NOTHING;

-- El admin también tiene rol CLIENTE (para pruebas de compra)
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u, roles r
WHERE u.email = 'admin@tienda.com' AND r.name = 'ROLE_CLIENTE'
ON CONFLICT DO NOTHING;


-- ============================================================
-- CATEGORIES
-- ============================================================
INSERT INTO categories (name, description) VALUES
('Electrónica',  'Teléfonos, computadores, accesorios tecnológicos'),
('Ropa',         'Camisetas, pantalones, calzado y accesorios de moda'),
('Hogar',        'Muebles, decoración y artículos para el hogar'),
('Deportes',     'Equipos y ropa para actividades deportivas'),
('Libros',       'Libros físicos y material educativo')
ON CONFLICT (name) DO NOTHING;


-- ============================================================
-- PRODUCTS  (2–3 por categoría, algunos inactivos / sin stock)
-- ============================================================
INSERT INTO products (category_id, name, description, price, stock, image_url, active) VALUES

-- Electrónica
((SELECT id FROM categories WHERE name='Electrónica'),
 'Smartphone XS Pro', 'Pantalla AMOLED 6.7", 256 GB, cámara 108 MP', 1299000.00, 15,
 'https://images.example.com/smartphone-xs-pro.jpg', TRUE),

((SELECT id FROM categories WHERE name='Electrónica'),
 'Auriculares Inalámbricos Z5', 'Cancelación activa de ruido, 30 h batería', 349000.00, 40,
 'https://images.example.com/auriculares-z5.jpg', TRUE),

((SELECT id FROM categories WHERE name='Electrónica'),
 'Tablet UltraSlim 10"', 'Procesador octa-core, 4 GB RAM, 64 GB', 799000.00, 0,   -- sin stock
 'https://images.example.com/tablet-ultraslim.jpg', TRUE),

-- Ropa
((SELECT id FROM categories WHERE name='Ropa'),
 'Camiseta Premium Algodón', 'Camiseta unisex 100% algodón peinado', 49900.00, 200,
 'https://images.example.com/camiseta-premium.jpg', TRUE),

((SELECT id FROM categories WHERE name='Ropa'),
 'Jeans Slim Fit Oscuro', 'Mezclilla stretch, corte slim, talla 28-40', 129900.00, 80,
 'https://images.example.com/jeans-slim.jpg', TRUE),

((SELECT id FROM categories WHERE name='Ropa'),
 'Chaqueta Vintage Cuero Sintético', 'Estilo retro, forro polar, tallas S-XXL', 259900.00, 25,
 'https://images.example.com/chaqueta-vintage.jpg', FALSE),  -- producto inactivo

-- Hogar
((SELECT id FROM categories WHERE name='Hogar'),
 'Lámpara LED Minimalista', 'Luz cálida/fría regulable, base metálica', 89900.00, 60,
 'https://images.example.com/lampara-led.jpg', TRUE),

((SELECT id FROM categories WHERE name='Hogar'),
 'Juego de Sábanas 500 Hilos', 'Algodón egipcio, tamaño queen, incluye fundas', 189900.00, 35,
 'https://images.example.com/sabanas-500h.jpg', TRUE),

-- Deportes
((SELECT id FROM categories WHERE name='Deportes'),
 'Tenis Running AirFlex', 'Suela de gel, transpirable, tallas 36-46', 219900.00, 50,
 'https://images.example.com/tenis-airflex.jpg', TRUE),

((SELECT id FROM categories WHERE name='Deportes'),
 'Mancuernas Ajustables 20 kg', 'Par de mancuernas con perilla regulable', 399900.00, 12,
 'https://images.example.com/mancuernas-20kg.jpg', TRUE),

-- Libros
((SELECT id FROM categories WHERE name='Libros'),
 'Clean Code - Robert C. Martin', 'Guía de buenas prácticas para escribir código limpio', 79900.00, 30,
 'https://images.example.com/clean-code.jpg', TRUE),

((SELECT id FROM categories WHERE name='Libros'),
 'Diseño de Sistemas Distribuidos', 'Patrones y paradigmas para sistemas escalables', 89900.00, 18,
 'https://images.example.com/distributed-systems.jpg', TRUE);


-- ============================================================
-- CARTS  (uno por usuario cliente activo)
-- ============================================================
INSERT INTO carts (user_id)
SELECT id FROM users WHERE email IN (
    'ana.garcia@mail.com',
    'carlos.m@mail.com',
    'lucia.r@mail.com',
    'pedro.h@mail.com'
)
ON CONFLICT (user_id) DO NOTHING;


-- ============================================================
-- CART_ITEMS  (varios productos en cada carrito)
-- ============================================================
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
-- Carrito de Ana: smartphone + auriculares
SELECT c.id,
       p.id,
       1,
       p.price
FROM carts c
JOIN users u ON u.id = c.user_id
JOIN products p ON p.name = 'Smartphone XS Pro'
WHERE u.email = 'ana.garcia@mail.com'
ON CONFLICT DO NOTHING;

INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT c.id, p.id, 2, p.price
FROM carts c
JOIN users u ON u.id = c.user_id
JOIN products p ON p.name = 'Auriculares Inalámbricos Z5'
WHERE u.email = 'ana.garcia@mail.com'
ON CONFLICT DO NOTHING;

-- Carrito de Carlos: libros
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT c.id, p.id, 1, p.price
FROM carts c
JOIN users u ON u.id = c.user_id
JOIN products p ON p.name = 'Clean Code - Robert C. Martin'
WHERE u.email = 'carlos.m@mail.com'
ON CONFLICT DO NOTHING;

INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT c.id, p.id, 1, p.price
FROM carts c
JOIN users u ON u.id = c.user_id
JOIN products p ON p.name = 'Diseño de Sistemas Distribuidos'
WHERE u.email = 'carlos.m@mail.com'
ON CONFLICT DO NOTHING;

-- Carrito de Lucía: ropa + deporte
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT c.id, p.id, 3, p.price
FROM carts c
JOIN users u ON u.id = c.user_id
JOIN products p ON p.name = 'Camiseta Premium Algodón'
WHERE u.email = 'lucia.r@mail.com'
ON CONFLICT DO NOTHING;

INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT c.id, p.id, 1, p.price
FROM carts c
JOIN users u ON u.id = c.user_id
JOIN products p ON p.name = 'Tenis Running AirFlex'
WHERE u.email = 'lucia.r@mail.com'
ON CONFLICT DO NOTHING;

-- Carrito de Pedro: hogar (un solo ítem)
INSERT INTO cart_items (cart_id, product_id, quantity, unit_price)
SELECT c.id, p.id, 1, p.price
FROM carts c
JOIN users u ON u.id = c.user_id
JOIN products p ON p.name = 'Juego de Sábanas 500 Hilos'
WHERE u.email = 'pedro.h@mail.com'
ON CONFLICT DO NOTHING;


-- ============================================================
-- COUPONS
-- ============================================================
INSERT INTO coupons (code, discount_type, discount_value, expires_at, active) VALUES
('BIENVENIDO10',  'PERCENTAGE', 10.00, NOW() + INTERVAL '30 days',  TRUE),   -- 10% activo
('VERANO2025',    'PERCENTAGE', 20.00, NOW() - INTERVAL '10 days',  FALSE),  -- expirado
('DESCUENTO50K',  'FIXED',      50000.00, NOW() + INTERVAL '7 days', TRUE),  -- fijo activo
('FLASH30',       'PERCENTAGE', 30.00, NULL,                         TRUE),   -- sin expiración
('INACTIVO',      'FIXED',      15000.00, NULL,                      FALSE)   -- cupón desactivado manualmente
ON CONFLICT (code) DO NOTHING;


-- ============================================================
-- ORDERS  (varios estados para cubrir el CHECK constraint)
-- ============================================================
-- Pedido PAID de Ana (con cupón)
INSERT INTO orders (user_id, coupon_id, status, subtotal, discount, total)
SELECT
    u.id,
    cp.id,
    'PAID',
    1997000.00,
    199700.00,
    1797300.00
FROM users u, coupons cp
WHERE u.email = 'ana.garcia@mail.com' AND cp.code = 'BIENVENIDO10';

-- Pedido SHIPPED de Ana (sin cupón)
INSERT INTO orders (user_id, coupon_id, status, subtotal, discount, total)
SELECT u.id, NULL, 'SHIPPED', 349000.00, 0.00, 349000.00
FROM users u WHERE u.email = 'ana.garcia@mail.com';

-- Pedido DELIVERED de Carlos
INSERT INTO orders (user_id, coupon_id, status, subtotal, discount, total)
SELECT u.id, NULL, 'DELIVERED', 169800.00, 0.00, 169800.00
FROM users u WHERE u.email = 'carlos.m@mail.com';

-- Pedido PENDING de Lucía (con cupón fijo)
INSERT INTO orders (user_id, coupon_id, status, subtotal, discount, total)
SELECT u.id, cp.id, 'PENDING', 369700.00, 50000.00, 319700.00
FROM users u, coupons cp
WHERE u.email = 'lucia.r@mail.com' AND cp.code = 'DESCUENTO50K';

-- Pedido CANCELLED de Pedro
INSERT INTO orders (user_id, coupon_id, status, subtotal, discount, total)
SELECT u.id, NULL, 'CANCELLED', 189900.00, 0.00, 189900.00
FROM users u WHERE u.email = 'pedro.h@mail.com';


-- ============================================================
-- ORDER_ITEMS
-- ============================================================
-- Items del pedido PAID de Ana (smartphone x1 + auriculares x2)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT o.id, p.id, 1, 1299000.00
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN products p ON p.name = 'Smartphone XS Pro'
WHERE u.email = 'ana.garcia@mail.com' AND o.status = 'PAID';

INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT o.id, p.id, 2, 349000.00
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN products p ON p.name = 'Auriculares Inalámbricos Z5'
WHERE u.email = 'ana.garcia@mail.com' AND o.status = 'PAID';

-- Items del pedido SHIPPED de Ana (auriculares x1)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT o.id, p.id, 1, 349000.00
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN products p ON p.name = 'Auriculares Inalámbricos Z5'
WHERE u.email = 'ana.garcia@mail.com' AND o.status = 'SHIPPED';

-- Items del pedido DELIVERED de Carlos (libros x1 c/u)
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT o.id, p.id, 1, 79900.00
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN products p ON p.name = 'Clean Code - Robert C. Martin'
WHERE u.email = 'carlos.m@mail.com' AND o.status = 'DELIVERED';

INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT o.id, p.id, 1, 89900.00
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN products p ON p.name = 'Diseño de Sistemas Distribuidos'
WHERE u.email = 'carlos.m@mail.com' AND o.status = 'DELIVERED';

-- Items del pedido PENDING de Lucía
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT o.id, p.id, 3, 49900.00
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN products p ON p.name = 'Camiseta Premium Algodón'
WHERE u.email = 'lucia.r@mail.com' AND o.status = 'PENDING';

INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT o.id, p.id, 1, 219900.00
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN products p ON p.name = 'Tenis Running AirFlex'
WHERE u.email = 'lucia.r@mail.com' AND o.status = 'PENDING';

-- Items del pedido CANCELLED de Pedro
INSERT INTO order_items (order_id, product_id, quantity, unit_price)
SELECT o.id, p.id, 1, 189900.00
FROM orders o
JOIN users u ON u.id = o.user_id
JOIN products p ON p.name = 'Juego de Sábanas 500 Hilos'
WHERE u.email = 'pedro.h@mail.com' AND o.status = 'CANCELLED';


-- ============================================================
-- PAYMENTS  (cubre los 4 status del CHECK constraint)
-- ============================================================
-- Pago APPROVED para el pedido PAID de Ana
INSERT INTO payments (order_id, mp_preference_id, mp_payment_id, status, amount, paid_at)
SELECT o.id,
       'pref_ANA_PAID_001',
       'mp_pay_111222333',
       'APPROVED',
       1797300.00,
       NOW() - INTERVAL '5 days'
FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.email = 'ana.garcia@mail.com' AND o.status = 'PAID';

-- Pago PENDING para el pedido SHIPPED de Ana
INSERT INTO payments (order_id, mp_preference_id, mp_payment_id, status, amount, paid_at)
SELECT o.id,
       'pref_ANA_SHIP_002',
       NULL,
       'PENDING',
       349000.00,
       NULL
FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.email = 'ana.garcia@mail.com' AND o.status = 'SHIPPED';

-- Pago APPROVED para el pedido DELIVERED de Carlos
INSERT INTO payments (order_id, mp_preference_id, mp_payment_id, status, amount, paid_at)
SELECT o.id,
       'pref_CAR_DEL_003',
       'mp_pay_444555666',
       'APPROVED',
       169800.00,
       NOW() - INTERVAL '15 days'
FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.email = 'carlos.m@mail.com' AND o.status = 'DELIVERED';

-- Pago PENDING para el pedido PENDING de Lucía
INSERT INTO payments (order_id, mp_preference_id, mp_payment_id, status, amount, paid_at)
SELECT o.id,
       'pref_LUC_PEND_004',
       NULL,
       'PENDING',
       319700.00,
       NULL
FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.email = 'lucia.r@mail.com' AND o.status = 'PENDING';

-- Pago REJECTED para el pedido CANCELLED de Pedro
INSERT INTO payments (order_id, mp_preference_id, mp_payment_id, status, amount, paid_at)
SELECT o.id,
       'pref_PED_CAN_005',
       'mp_pay_777888999',
       'REJECTED',
       189900.00,
       NULL
FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.email = 'pedro.h@mail.com' AND o.status = 'CANCELLED';

-- Pago CANCELLED extra (para probar el 4to status)
INSERT INTO payments (order_id, mp_preference_id, mp_payment_id, status, amount, paid_at)
SELECT o.id,
       'pref_PED_CAN_006',
       NULL,
       'CANCELLED',
       189900.00,
       NULL
FROM orders o
JOIN users u ON u.id = o.user_id
WHERE u.email = 'pedro.h@mail.com' AND o.status = 'CANCELLED';


-- ============================================================
-- REVIEWS  (solo usuarios con pedido DELIVERED o PAID pueden reseñar)
-- Un usuario no puede reseñar el mismo producto dos veces (UNIQUE)
-- ============================================================
INSERT INTO reviews (user_id, product_id, rating, comment)
SELECT u.id, p.id, 5,
       'Excelente smartphone, cámara increíble y batería que dura todo el día. 100% recomendado.'
FROM users u, products p
WHERE u.email = 'ana.garcia@mail.com' AND p.name = 'Smartphone XS Pro'
ON CONFLICT DO NOTHING;

INSERT INTO reviews (user_id, product_id, rating, comment)
SELECT u.id, p.id, 4,
       'Muy buenos auriculares, la cancelación de ruido funciona bien. Le falta un poco de bajos.'
FROM users u, products p
WHERE u.email = 'ana.garcia@mail.com' AND p.name = 'Auriculares Inalámbricos Z5'
ON CONFLICT DO NOTHING;

INSERT INTO reviews (user_id, product_id, rating, comment)
SELECT u.id, p.id, 5,
       'El mejor libro que he leído sobre buenas prácticas. Lectura obligatoria para todo dev.'
FROM users u, products p
WHERE u.email = 'carlos.m@mail.com' AND p.name = 'Clean Code - Robert C. Martin'
ON CONFLICT DO NOTHING;

INSERT INTO reviews (user_id, product_id, rating, comment)
SELECT u.id, p.id, 3,
       'Contenido interesante pero algunos ejemplos ya están desactualizados.'
FROM users u, products p
WHERE u.email = 'carlos.m@mail.com' AND p.name = 'Diseño de Sistemas Distribuidos'
ON CONFLICT DO NOTHING;

INSERT INTO reviews (user_id, product_id, rating, comment)
SELECT u.id, p.id, 2,
       'La tela no es lo que esperaba, se ve diferente a las fotos. Calidad mediocre.'
FROM users u, products p
WHERE u.email = 'lucia.r@mail.com' AND p.name = 'Camiseta Premium Algodón'
ON CONFLICT DO NOTHING;

INSERT INTO reviews (user_id, product_id, rating, comment)
SELECT u.id, p.id, 1,
       'Llegaron rotas. El servicio al cliente no respondió. Muy mala experiencia.'
FROM users u, products p
WHERE u.email = 'pedro.h@mail.com' AND p.name = 'Juego de Sábanas 500 Hilos'
ON CONFLICT DO NOTHING;

-- Reseña sin comentario (solo rating) — prueba el campo TEXT nullable
INSERT INTO reviews (user_id, product_id, rating, comment)
SELECT u.id, p.id, 4, NULL
FROM users u, products p
WHERE u.email = 'admin@tienda.com' AND p.name = 'Smartphone XS Pro'
ON CONFLICT DO NOTHING;


-- ============================================================
-- VERIFICACIÓN RÁPIDA (opcional — ejecutar por separado)
-- ============================================================
-- SELECT 'roles'       AS tabla, COUNT(*) AS filas FROM roles
-- UNION ALL SELECT 'users',       COUNT(*) FROM users
-- UNION ALL SELECT 'user_roles',  COUNT(*) FROM user_roles
-- UNION ALL SELECT 'categories',  COUNT(*) FROM categories
-- UNION ALL SELECT 'products',    COUNT(*) FROM products
-- UNION ALL SELECT 'carts',       COUNT(*) FROM carts
-- UNION ALL SELECT 'cart_items',  COUNT(*) FROM cart_items
-- UNION ALL SELECT 'coupons',     COUNT(*) FROM coupons
-- UNION ALL SELECT 'orders',      COUNT(*) FROM orders
-- UNION ALL SELECT 'order_items', COUNT(*) FROM order_items
-- UNION ALL SELECT 'payments',    COUNT(*) FROM payments
-- UNION ALL SELECT 'reviews',     COUNT(*) FROM reviews;
