-- Script para la inserción de datos inciales para la aplicación de motor de recomendaciones
-- Este script inserta datos iniciales para testing y desarrollo
-- Inserción de datos en la tabla usuarios
-- Nota: Las contraseñas deben ser hasheadas antes de la inserción en producción
-- Utilizaremos la contraseña 123 hasheada como ejemplo "$2a$12$xyDXamT1EKRxSsqZenHV0.NOUDL.YPB7dGF309j0NOtSbB1jsrCPa"
-- ================================
-- Tabla: users
-- ================================
INSERT INTO users (id, username, email, password, role, active, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 'Alex', 'Alex@recomendaciones.com', '$2a$12$xyDXamT1EKRxSsqZenHV0.NOUDL.YPB7dGF309j0NOtSbB1jsrCPa','ADMIN',true,now()),
('22222222-2222-2222-2222-222222222222', 'Lucas', 'Lucas@recomendaciones.com', '$2a$12$xyDXamT1EKRxSsqZenHV0.NOUDL.YPB7dGF309j0NOtSbB1jsrCPa', 'PLAYER',true,now()),
('33333333-3333-3333-3333-333333333333', 'Ruddy', 'Ruddy@recomendaciones.com', '$2a$12$xyDXamT1EKRxSsqZenHV0.NOUDL.YPB7dGF309j0NOtSbB1jsrCPa','ADMIN', true,now()),
('44444444-4444-4444-4444-444444444444', 'Juan', 'Juan@recomendaciones.com', '$2a$12$xyDXamT1EKRxSsqZenHV0.NOUDL.YPB7dGF309j0NOtSbB1jsrCPa','PLAYER',true,now());
-- ================================
-- Tabla: product
-- ================================
INSERT INTO products (id, name, description, category, popularity_score)
VALUES
('a1111111-aaaa-1111-aaaa-111111111111', 'Valorant Champions Hoodie', 'Sudadera oficial del torneo Valorant Champions 2025, edición limitada.', 'Merchandising', 2500),
('a2222222-aaaa-2222-aaaa-222222222222', 'Valorant Pro Series Mouse', 'Ratón gamer edición Pro Series usado en el torneo oficial.', 'Periférico', 1800),
('a3333333-aaaa-3333-aaaa-333333333333', 'Valorant Champions Skin Pack', 'Paquete de skins exclusivo del evento internacional.', 'Digital Item', 3200),
('a4444444-aaaa-4444-aaaa-444444444444', 'Valorant Masters Entry Ticket', 'Entrada general para el torneo Valorant Masters.', 'Evento', 4500),
('a5555555-aaaa-5555-aaaa-555555555555', 'Valorant Collector Figure - Jett', 'Figura coleccionable oficial de Jett, edición Champions.', 'Coleccionable', 1200);
-- ================================
-- Tabla: product_tags
-- ================================
INSERT INTO product_tags (product_id, tags) VALUES
('a1111111-aaaa-1111-aaaa-111111111111', 'valorant'),
('a1111111-aaaa-1111-aaaa-111111111111', 'merch'),
('a1111111-aaaa-1111-aaaa-111111111111', 'ropa'),
('a1111111-aaaa-1111-aaaa-111111111111', 'torneo'),
('a2222222-aaaa-2222-aaaa-222222222222', 'periferico'),
('a2222222-aaaa-2222-aaaa-222222222222', 'valorant'),
('a2222222-aaaa-2222-aaaa-222222222222', 'gaming'),
('a2222222-aaaa-2222-aaaa-222222222222', 'torneo'),
('a3333333-aaaa-3333-aaaa-333333333333', 'digital'),
('a3333333-aaaa-3333-aaaa-333333333333', 'valorant'),
('a3333333-aaaa-3333-aaaa-333333333333', 'skins'),
('a3333333-aaaa-3333-aaaa-333333333333', 'evento');
-- ================================
-- Tabla: ratings
-- ================================
INSERT INTO ratings (id, score, user_id, product_id, created_at) VALUES
('11111111-1111-1111-1111-111111111111', 5, '11111111-1111-1111-1111-111111111111', 'a1111111-aaaa-1111-aaaa-111111111111', NOW()),
('11111111-1111-1111-1111-111111111112', 4, '11111111-1111-1111-1111-111111111111', 'a2222222-aaaa-2222-aaaa-222222222222', NOW()),
('22222222-2222-2222-2222-222222222221', 5, '22222222-2222-2222-2222-222222222222', 'a3333333-aaaa-3333-aaaa-333333333333', NOW()),
('22222222-2222-2222-2222-222222222222', 3, '22222222-2222-2222-2222-222222222222', 'a4444444-aaaa-4444-aaaa-444444444444', NOW()),
('33333333-3333-3333-3333-333333333331', 4, '33333333-3333-3333-3333-333333333333', 'a1111111-aaaa-1111-aaaa-111111111111', NOW()),
('33333333-3333-3333-3333-333333333332', 5, '33333333-3333-3333-3333-333333333333', 'a5555555-aaaa-5555-aaaa-555555555555', NOW()),
('44444444-4444-4444-4444-444444444441', 4, '44444444-4444-4444-4444-444444444444', 'a2222222-aaaa-2222-aaaa-222222222222', NOW()),
('44444444-4444-4444-4444-444444444442', 3, '44444444-4444-4444-4444-444444444444', 'a4444444-aaaa-4444-aaaa-444444444444', NOW());
-- ================================
-- Tabla: recommendations
-- ================================
INSERT INTO recommendations (id, user_id, algorithm_version, computed_at) VALUES 
('11111111-1111-1111-1111-111111111111', '11111111-1111-1111-1111-111111111111', 'v1.0', NOW()), 
('22222222-2222-2222-2222-222222222222', '22222222-2222-2222-2222-222222222222', 'v1.0', NOW()), 
('33333333-3333-3333-3333-333333333333', '33333333-3333-3333-3333-333333333333', 'v1.0', NOW()), 
('44444444-4444-4444-4444-444444444444', '44444444-4444-4444-4444-444444444444', 'v1.0', NOW());
-- ================================
-- Tabla Intermedia: product_recommendations
-- ================================
insert into product_recommendations(recommendation_id, product_id) values
('11111111-1111-1111-1111-111111111111', 'a1111111-aaaa-1111-aaaa-111111111111'),
('11111111-1111-1111-1111-111111111111', 'a2222222-aaaa-2222-aaaa-222222222222'),
('22222222-2222-2222-2222-222222222222', 'a3333333-aaaa-3333-aaaa-333333333333'),
('33333333-3333-3333-3333-333333333333', 'a1111111-aaaa-1111-aaaa-111111111111'),
('33333333-3333-3333-3333-333333333333', 'a5555555-aaaa-5555-aaaa-555555555555'),
('44444444-4444-4444-4444-444444444444', 'a4444444-aaaa-4444-aaaa-444444444444');

