-- Desactivar temporalmente restricciones de clave foránea
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE transacciones;
TRUNCATE TABLE cuentas_bancarias;
TRUNCATE TABLE clientes;

SET FOREIGN_KEY_CHECKS = 1;

-- =============================================================================
-- 1. INSERCIÓN DE CLIENTES
-- =============================================================================
INSERT INTO clientes (id, nombre, cuil, email, fecha_creacion, fecha_ultima_modificacion) VALUES
(1, 'Juan Pérez', '20301112229', 'juan.perez@email.com', NOW(), NOW()),
(2, 'María Gómez', '27312223334', 'maria.gomez@email.com', NOW(), NOW()),
(3, 'Carlos López', '20323334445', 'carlos.lopez@email.com', NOW(), NOW()),
(4, 'Ana Martínez', '27334445556', 'ana.martinez@email.com', NOW(), NOW()),
(5, 'Luis Rodríguez', '20345556667', 'luis.rodriguez@email.com', NOW(), NOW()),
(6, 'Sofía Fernández', '27356667778', 'sofia.fernandez@email.com', NOW(), NOW()),
(7, 'Diego Sánchez', '20367778889', 'diego.sanchez@email.com', NOW(), NOW()),
(8, 'Laura Pérez', '27378889990', 'laura.perez@email.com', NOW(), NOW()),
(9, 'Martín González', '20389990001', 'martin.gonzalez@email.com', NOW(), NOW()),
(10, 'Lucía Romero', '27390001112', 'lucia.romero@email.com', NOW(), NOW());

-- =============================================================================
-- 2. INSERCIÓN DE CUENTAS BANCARIAS
-- =============================================================================
INSERT INTO cuentas_bancarias (id, cbu, alias, saldo, estado, cliente_id, fecha_creacion, fecha_ultima_modificacion) VALUES
(1, '0000003100000000000001', 'JUAN.PEREZ.ARS', 150000.50, 'ACTIVA', 1, NOW(), NOW()),
(2, '0000003100000000000002', 'JUAN.PEREZ.USD', 85000.00, 'ACTIVA', 1, NOW(), NOW()),
(3, '0000003100000000000003', 'MARIA.GOMEZ.ARS', 320000.75, 'ACTIVA', 2, NOW(), NOW()),
(4, '0000003100000000000004', 'CARLOS.LOPEZ.ARS', 45000.00, 'SUSPENDIDA', 3, NOW(), NOW()),
(5, '0000003100000000000005', 'ANA.MARTINEZ.ARS', 920000.00, 'ACTIVA', 4, NOW(), NOW()),
(6, '0000003100000000000006', 'LUIS.RODRIGUEZ.ARS', 12500.20, 'BLOQUEADA', 5, NOW(), NOW()),
(7, '0000003100000000000007', 'SOFIA.FERNANDEZ.ARS', 67000.00, 'ACTIVA', 6, NOW(), NOW()),
(8, '0000003100000000000008', 'DIEGO.SANCHEZ.ARS', 540000.10, 'ACTIVA', 7, NOW(), NOW()),
(9, '0000003100000000000009', 'LAURA.PEREZ.ARS', 23000.00, 'ACTIVA', 8, NOW(), NOW()),
(10, '0000003100000000000010', 'MARTIN.GONZALEZ.ARS', 115000.80, 'ACTIVA', 9, NOW(), NOW()),
(11, '0000003100000000000011', 'LUCIA.ROMERO.ARS', 890000.00, 'ACTIVA', 10, NOW(), NOW());

-- =============================================================================
-- 3. INSERCIÓN DE TRANSACCIONES
-- =============================================================================
INSERT INTO transacciones (id, monto, tipo_transaccion, estado_transaccion, cuenta_bancaria_id, fecha_creacion, fecha_ultima_modificacion) VALUES
(1, 50000.00, 'DEPOSITO', 'COMPLETADA', 1, NOW(), NOW()),
(2, 12000.50, 'EXTRACCION', 'COMPLETADA', 1, NOW(), NOW()),
(3, 100000.00, 'TRANSFERENCIA_RECIBIDA', 'COMPLETADA', 2, NOW(), NOW()),
(4, 150000.00, 'DEPOSITO', 'COMPLETADA', 3, NOW(), NOW()),
(5, 5000.00, 'EXTRACCION', 'PENDIENTE', 4, NOW(), NOW()),
(6, 250000.00, 'TRANSFERENCIA_RECIBIDA', 'COMPLETADA', 5, NOW(), NOW()),
(7, 2500.00, 'EXTRACCION', 'RECHAZADA', 6, NOW(), NOW()),
(8, 30000.00, 'DEPOSITO', 'COMPLETADA', 7, NOW(), NOW()),
(9, 100000.00, 'TRANSFERENCIA_ENVIADA', 'COMPLETADA', 8, NOW(), NOW()),
(10, 5000.00, 'DEPOSITO', 'COMPLETADA', 9, NOW(), NOW()),
(11, 45000.00, 'TRANSFERENCIA_RECIBIDA', 'COMPLETADA', 10, NOW(), NOW()),
(12, 120000.00, 'DEPOSITO', 'COMPLETADA', 11, NOW(), NOW());