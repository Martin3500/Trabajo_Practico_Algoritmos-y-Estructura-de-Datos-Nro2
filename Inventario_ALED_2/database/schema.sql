-- ========================================================
-- SISTEMA DE GESTIÓN DE INVENTARIO - SCRIPT DE BASE DE DATOS
-- Base de Datos: MySQL / MariaDB
-- ========================================================

CREATE DATABASE IF NOT EXISTS inventario_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE inventario_db;

-- 1. TABLA: ROLES (1 a N con usuarios)
CREATE TABLE IF NOT EXISTS roles (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. TABLA: USUARIOS (N a 1 con roles, 1 a N con productos)
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    id_rol INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL DEFAULT '1234',
    email VARCHAR(100) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuarios_roles FOREIGN KEY (id_rol) 
        REFERENCES roles(id_rol) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. TABLA: CATEGORIAS (1 a N con productos)
CREATE TABLE IF NOT EXISTS categorias (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. TABLA: PRODUCTOS (N a 1 con categorias, N a 1 con usuarios, 1 a N con movimientos_inventario)
CREATE TABLE IF NOT EXISTS productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    id_categoria INT NOT NULL,
    id_usuario INT NOT NULL,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10, 2) NOT NULL DEFAULT 0.00,
    stock INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_productos_categorias FOREIGN KEY (id_categoria) 
        REFERENCES categorias(id_categoria) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT,
    CONSTRAINT fk_productos_usuarios FOREIGN KEY (id_usuario) 
        REFERENCES usuarios(id_usuario) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 5. TABLA: TIPOS_MOVIMIENTO (1 a N con movimientos_inventario)
CREATE TABLE IF NOT EXISTS tipos_movimiento (
    id_tipo INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 6. TABLA: MOVIMIENTOS_INVENTARIO (N a 1 con tipos_movimiento, N a 1 con productos)
CREATE TABLE IF NOT EXISTS movimientos_inventario (
    id_movimiento INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    id_tipo INT NOT NULL,
    cantidad INT NOT NULL,
    motivo VARCHAR(255),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_movimientos_productos FOREIGN KEY (id_producto) 
        REFERENCES productos(id_producto) 
        ON UPDATE CASCADE 
        ON DELETE CASCADE,
    CONSTRAINT fk_movimientos_tipos FOREIGN KEY (id_tipo) 
        REFERENCES tipos_movimiento(id_tipo) 
        ON UPDATE CASCADE 
        ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ========================================================
-- DATOS INICIALES POR DEFECTO (SEMILLA)
-- ========================================================

-- Roles iniciales
INSERT INTO roles (id_rol, nombre, descripcion) VALUES
(1, 'Administrador', 'Acceso total y control del inventario y usuarios'),
(2, 'Almacenero', 'Control de existencias y registro de movimientos de almacén'),
(3, 'Supervisor', 'Visualización de reportes y supervisión de stock')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);

-- Tipos de movimiento estándar
INSERT INTO tipos_movimiento (id_tipo, nombre, descripcion) VALUES
(1, 'ENTRADA', 'Ingreso de mercadería por compras o devolución'),
(2, 'SALIDA', 'Egreso de mercadería por venta o consumo interno'),
(3, 'AJUSTE', 'Ajuste de inventario por auditoría o conteo físico')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);

-- Categorías iniciales
INSERT INTO categorias (id_categoria, nombre, descripcion) VALUES
(1, 'Electrónica', 'Dispositivos y componentes electrónicos'),
(2, 'Computación', 'Laptops, accesorios y periféricos'),
(3, 'Oficina', 'Artículos y suministros de oficina')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);

-- Usuario inicial
INSERT INTO usuarios (id_usuario, id_rol, nombre, username, password, email) VALUES
(1, 1, 'Administrador Principal', 'admin', 'admin', 'admin@empresa.com'),
(2, 2, 'Juan Pérez (Almacén)', 'jperez', '1234', 'jperez@empresa.com')
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), password = VALUES(password), email = VALUES(email);

-- Productos iniciales
INSERT INTO productos (id_producto, id_categoria, id_usuario, codigo, nombre, descripcion, precio, stock) VALUES
(1, 2, 1, 'PROD-001', 'Mouse Inalámbrico Logitech', 'Mouse ergonómico 2.4GHz', 19.99, 50),
(2, 2, 1, 'PROD-002', 'Teclado Mecánico RGB', 'Teclado switches Blue español', 49.50, 25),
(3, 1, 2, 'PROD-003', 'Cable HDMI 2.1 4K 2m', 'Cable mallado ultra alta velocidad', 12.00, 80)
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), precio = VALUES(precio);

