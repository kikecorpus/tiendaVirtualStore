-- ============================================================
-- V8 - Datos semilla
-- Sprint 5 | Usuario admin inicial + categorías de ejemplo
-- ============================================================

-- Contraseña: Admin123!
-- Hash generado con BCrypt rounds=12. Para regenerarlo:
--   En Java:  new BCryptPasswordEncoder().encode("Admin123!")
--   En Python: bcrypt.hashpw(b"Admin123!", bcrypt.gensalt(rounds=12))
--
-- ⚠️  EN PRODUCCIÓN: reemplaza este hash por uno generado localmente
--     o mejor aún, usa una variable de entorno y configúralo
--     desde la app al arrancar, no desde SQL.
INSERT INTO users (email, password_hash, first_name, last_name, enabled)
VALUES (
    'admin@tienda.com',
    '$2b$12$bvk7OgpsLQhpfZI8MRx.ve1M1rZJF2ecYvI0t3d5hp13QqQ6wlg5y',
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