package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionBD {

    public static String HOST = "localhost";
    public static String PUERTO = "3306";
    public static String BD_NAME = "inventario_db";
    public static String USUARIO = "root";
    public static String PASSWORD = "";
    private static final String PARAMS = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";

    // Lista de contraseñas típicas de entornos de estudio (XAMPP, MySQL Workbench, WAMP, MAMP)
    private static final String[] PASSWORDS_COMUNES = {"", "root", "1234", "123456", "admin", "mysql"};

    private static String getUrlServer() {
        return "jdbc:mysql://" + HOST + ":" + PUERTO + "/?" + PARAMS;
    }

    private static String getUrlBd() {
        return "jdbc:mysql://" + HOST + ":" + PUERTO + "/" + BD_NAME + "?" + PARAMS;
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar Driver MySQL: " + e.getMessage());
        }
    }

    /**
     * Obtiene una conexión activa a la base de datos inventario_db.
     */
    public static Connection getConexion() throws SQLException {
        return DriverManager.getConnection(getUrlBd(), USUARIO, PASSWORD);
    }

    /**
     * Intenta conectarse automáticamente probando las contraseñas más comunes.
     * Si ninguna funciona, pide los datos en una ventana rápida para no tener que tocar el código.
     */
    private static void autodiscoverCredenciales() throws SQLException {
        SQLException ultimoError = null;

        // 1. Probar con la configuración actual y luego con las contraseñas típicas
        for (String pass : PASSWORDS_COMUNES) {
            try (Connection conn = DriverManager.getConnection(getUrlServer(), USUARIO, pass)) {
                PASSWORD = pass;
                System.out.println("[✓] Conexión exitosa con usuario '" + USUARIO + "' y password '" + (pass.isEmpty() ? "(vacío)" : pass) + "'");
                return;
            } catch (SQLException e) {
                ultimoError = e;
            }
        }

        // 2. Si fallan las automáticas, mostrar cuadro simple para ingresar usuario/contraseña/puerto
        javax.swing.JTextField txtHost = new javax.swing.JTextField(HOST);
        javax.swing.JTextField txtPuerto = new javax.swing.JTextField(PUERTO);
        javax.swing.JTextField txtUsuario = new javax.swing.JTextField(USUARIO);
        javax.swing.JTextField txtPassword = new javax.swing.JTextField(PASSWORD);

        Object[] mensaje = {
            "No se pudo entrar con las claves por defecto.\nIngresa los datos de tu MySQL local:",
            "Host:", txtHost,
            "Puerto:", txtPuerto,
            "Usuario:", txtUsuario,
            "Contraseña:", txtPassword
        };

        int opcion = javax.swing.JOptionPane.showConfirmDialog(
            null, mensaje, "Configuración Rápida de Base de Datos", javax.swing.JOptionPane.OK_CANCEL_OPTION
        );

        if (opcion == javax.swing.JOptionPane.OK_OPTION) {
            HOST = txtHost.getText().trim();
            PUERTO = txtPuerto.getText().trim();
            USUARIO = txtUsuario.getText().trim();
            PASSWORD = txtPassword.getText();
            // Probar con los datos ingresados
            try (Connection conn = DriverManager.getConnection(getUrlServer(), USUARIO, PASSWORD)) {
                return;
            }
        }

        if (ultimoError != null) {
            throw ultimoError;
        }
    }

    /**
     * Inicializa la base de datos y sus tablas automáticamente si no existen.
     */
    public static void inicializarBaseDeDatos() throws SQLException {
        // 0. Detectar credenciales automáticamente
        autodiscoverCredenciales();

        // 1. Crear base de datos si no existe
        try (Connection conn = DriverManager.getConnection(getUrlServer(), USUARIO, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + BD_NAME + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
        }

        // 2. Conectarse a la BD y crear tablas y datos semilla
        try (Connection conn = DriverManager.getConnection(getUrlBd(), USUARIO, PASSWORD);
             Statement stmt = conn.createStatement()) {

            // Tabla Roles
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS roles (
                    id_rol INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(50) NOT NULL UNIQUE,
                    descripcion VARCHAR(255)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """);

            // Tabla Usuarios
            stmt.executeUpdate("""
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
            """);

            // Asegurar columna password si la tabla ya existía previamente
            try {
                stmt.executeUpdate("ALTER TABLE usuarios ADD COLUMN password VARCHAR(100) NOT NULL DEFAULT '1234'");
            } catch (SQLException ignored) {}

            // Tabla Categorias
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS categorias (
                    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(100) NOT NULL UNIQUE,
                    descripcion VARCHAR(255)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """);

            // Tabla Productos
            stmt.executeUpdate("""
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
            """);

            // Tabla Tipos de Movimiento
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS tipos_movimiento (
                    id_tipo INT AUTO_INCREMENT PRIMARY KEY,
                    nombre VARCHAR(50) NOT NULL UNIQUE,
                    descripcion VARCHAR(255)
                ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
            """);

            // Tabla Movimientos de Inventario
            stmt.executeUpdate("""
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
            """);

            // Datos semilla
            stmt.executeUpdate("""
                INSERT INTO roles (id_rol, nombre, descripcion) VALUES
                (1, 'Administrador', 'Acceso total y control del inventario y usuarios'),
                (2, 'Almacenero', 'Control de existencias y registro de movimientos de almacén'),
                (3, 'Supervisor', 'Visualización de reportes y supervisión de stock')
                ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);
            """);

            stmt.executeUpdate("""
                INSERT INTO tipos_movimiento (id_tipo, nombre, descripcion) VALUES
                (1, 'ENTRADA', 'Ingreso de mercadería por compras o devolución'),
                (2, 'SALIDA', 'Egreso de mercadería por venta o consumo interno'),
                (3, 'AJUSTE', 'Ajuste de inventario por auditoría o conteo físico')
                ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);
            """);

            stmt.executeUpdate("""
                INSERT INTO categorias (id_categoria, nombre, descripcion) VALUES
                (1, 'Electrónica', 'Dispositivos y componentes electrónicos'),
                (2, 'Computación', 'Laptops, accesorios y periféricos'),
                (3, 'Oficina', 'Artículos y suministros de oficina')
                ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion);
            """);

            stmt.executeUpdate("""
                INSERT INTO usuarios (id_usuario, id_rol, nombre, username, password, email) VALUES
                (1, 1, 'Administrador Principal', 'admin', 'admin', 'admin@empresa.com'),
                (2, 2, 'Juan Pérez (Almacén)', 'jperez', '1234', 'jperez@empresa.com')
                ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), password = VALUES(password), email = VALUES(email);
            """);

            stmt.executeUpdate("""
                INSERT INTO productos (id_producto, id_categoria, id_usuario, codigo, nombre, descripcion, precio, stock) VALUES
                (1, 2, 1, 'PROD-001', 'Mouse Inalámbrico Logitech', 'Mouse ergonómico 2.4GHz', 19.99, 50),
                (2, 2, 1, 'PROD-002', 'Teclado Mecánico RGB', 'Teclado switches Blue español', 49.50, 25),
                (3, 1, 2, 'PROD-003', 'Cable HDMI 2.1 4K 2m', 'Cable mallado ultra alta velocidad', 12.00, 80)
                ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), precio = VALUES(precio);
            """);
        }
    }
}

