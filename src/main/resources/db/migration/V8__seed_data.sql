-- ============================================================
-- V8 - Datos semilla
-- Sprint 5 | Usuario admin inicial + categorías de ejemplo
-- ============================================================

-- ⚠️  IMPORTANTE: Este password es solo para desarrollo local.
--     En producción usar un hash BCrypt real generado por la app.
--     Hash de ejemplo para: Admin123!
INSERT INTO users (email, password_hash, first_name, last_name, enabled)
VALUES (
    'admin@tienda.com',
    '$2a$12$eImiTXuWVxfM37uY4JANjQ==.example.hash.change.in.production',
    'Admin',
    'Tienda',
    TRUE
);

-- Asignar rol ADMIN al usuario recién creado
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u, roles r
WHERE u.email = 'admin@tienda.com'
  AND r.name  = 'ROLE_ADMIN';

-- Categorías de ejemplo para empezar a probar
INSERT INTO categories (name, description) VALUES
    ('Electrónica',    'Teléfonos, computadores, accesorios tecnológicos'),
    ('Ropa',           'Camisetas, pantalones, calzado y accesorios de moda'),
    ('Hogar',          'Muebles, decoración y artículos para el hogar'),
    ('Deportes',       'Equipos y ropa para actividades deportivas'),
    ('Libros',         'Libros físicos y material educativo');
