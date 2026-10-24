# Sistema de Gestión de Inventario

Aplicación de escritorio en **Java (Swing)** conectada a **MySQL / MariaDB** mediante JDBC, diseñada con arquitectura por capas (Modelo - DAO - Servicio - Interfaz de Usuario).

---

## 🗄️ Modelo Relacional de la Base de Datos

El sistema implementa exactamente las relaciones solicitadas:

1. **`roles` (1 a N) `usuarios`**: Un rol puede estar asignado a varios usuarios (`fk_usuarios_roles`).
2. **`usuarios` (1 a N) `productos`**: Un usuario registra y es responsable de múltiples productos (`fk_productos_usuarios`).
3. **`categorias` (1 a N) `productos`**: Una categoría agrupa múltiples productos (`fk_productos_categorias`).
4. **`tipos_movimiento` (1 a N) `movimientos_inventario`**: Cada movimiento corresponde a un tipo específico (`ENTRADA`, `SALIDA`, `AJUSTE`).
5. **`productos` (1 a N) `movimientos_inventario`**: Cada producto tiene su historial completo de movimientos (`fk_movimientos_productos`).

El script SQL completo y documentado se encuentra en:
📁 [`database/schema.sql`](database/schema.sql)

> **Nota:** La aplicación cuenta con **auto-inicialización**: al iniciar crea la base de datos `inventario_db`, todas sus tablas, restricciones de clave foránea y datos iniciales de prueba si aún no existen.

---

## 📁 Estructura del Proyecto

```
Inventario_ALED_2/
│
├── database/
│   └── schema.sql                  # Script SQL DDL y datos de prueba
│
├── lib/
│   └── mysql-connector-j-9.7.0.jar # Driver JDBC MySQL
│
├── src/
│   ├── config/
│   │   └── ConexionBD.java         # Conexión JDBC y creación automática de BD
│   ├── model/
│   │   ├── Rol.java
│   │   ├── Usuario.java
│   │   ├── Categoria.java
│   │   ├── Producto.java
│   │   ├── TipoMovimiento.java
│   │   └── MovimientoInventario.java
│   ├── dao/
│   │   ├── RolDAO.java
│   │   ├── UsuarioDAO.java
│   │   ├── CategoriaDAO.java
│   │   ├── ProductoDAO.java
│   │   ├── TipoMovimientoDAO.java
│   │   └── MovimientoInventarioDAO.java
│   ├── service/
│   │   └── InventarioService.java  # Lógica transaccional y actualización de stock
│   ├── ui/
│   │   ├── LoginFrame.java         # Ventana de Inicio de Sesión
│   │   ├── MainFrame.java          # Ventana principal (Pestañas de gestión)
│   │   ├── ProductoDialog.java     # Formulario de Producto
│   │   ├── MovimientoDialog.java   # Formulario de Movimientos
│   │   ├── CategoriaDialog.java    # Formulario de Categorías
│   │   └── UsuarioDialog.java      # Formulario de Usuarios
│   └── App.java                    # Punto de entrada de la aplicación
│
└── bin/                            # Archivos .class compilados
```

---

## 🔐 Control de Acceso y Credenciales de Prueba

La aplicación cuenta con inicio de sesión y control de permisos basado en el rol del usuario:

| Usuario | Contraseña | Rol | Permisos |
|---|---|---|---|
| **`admin`** | `admin` | **Administrador** | **Acceso Total**: Puede registrar, editar y eliminar nuevos usuarios, productos, categorías y movimientos. |
| **`jperez`** | `1234` | **Almacenero** | **Operador**: Puede registrar productos, movimientos y categorías. **No puede** agregar ni modificar usuarios (botones bloqueados). |

---

## ⚙️ Requisitos y Configuración

- **Java JDK 17 o superior** (o JDK 21).
- **MySQL / XAMPP**: Servicio MySQL corriendo en el puerto por defecto `3306` (usuario `root`, sin contraseña).

---

## 🚀 Cómo Ejecutar la Aplicación

### Desde VS Code:
1. Abre el archivo [`src/App.java`](src/App.java).
2. Presiona `F5` o haz clic en **Run Java**.

### O desde Terminal PowerShell:
```powershell
# Compilar:
javac -encoding UTF-8 -cp "lib\mysql-connector-j-9.7.0.jar;src" -d bin (Get-ChildItem -Path src -Filter *.java -Recurse | ForEach-Object { $_.FullName })

# Ejecutar:
java -cp "bin;lib\mysql-connector-j-9.7.0.jar" App
```
