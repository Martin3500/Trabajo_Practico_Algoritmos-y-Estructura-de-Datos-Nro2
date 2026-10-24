package service;

import config.ConexionBD;
import dao.MovimientoInventarioDAO;
import dao.ProductoDAO;
import model.MovimientoInventario;
import model.Producto;

import java.sql.Connection;
import java.sql.SQLException;

public class InventarioService {

    private final ProductoDAO productoDAO;
    private final MovimientoInventarioDAO movimientoDAO;

    public InventarioService() {
        this.productoDAO = new ProductoDAO();
        this.movimientoDAO = new MovimientoInventarioDAO();
    }

    /**
     * Registra un movimiento de inventario dentro de una transacción atómica
     * y actualiza el stock del producto de acuerdo al tipo de movimiento.
     */
    public void registrarMovimiento(int idProducto, int idTipo, String tipoNombre, int cantidad, String motivo) throws Exception {
        if (cantidad <= 0 && !"AJUSTE".equalsIgnoreCase(tipoNombre)) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0.");
        }
        if (cantidad < 0 && "AJUSTE".equalsIgnoreCase(tipoNombre)) {
            throw new IllegalArgumentException("El stock no puede ser un número negativo.");
        }

        Producto prod = productoDAO.obtenerPorId(idProducto);
        if (prod == null) {
            throw new IllegalArgumentException("El producto seleccionado no existe.");
        }

        int stockActual = prod.getStock();
        int nuevoStock;

        String tipoNorm = tipoNombre.toUpperCase().trim();
        switch (tipoNorm) {
            case "ENTRADA":
                nuevoStock = stockActual + cantidad;
                break;
            case "SALIDA":
                if (stockActual < cantidad) {
                    throw new IllegalStateException("Stock insuficiente. Stock actual: " + stockActual + ", Cantidad solicitada: " + cantidad);
                }
                nuevoStock = stockActual - cantidad;
                break;
            case "AJUSTE":
                // En ajuste, se define directamente el stock físico contado
                nuevoStock = cantidad;
                break;
            default:
                throw new IllegalArgumentException("Tipo de movimiento desconocido: " + tipoNombre);
        }

        // Ejecutar en transacción SQL
        try (Connection conn = ConexionBD.getConexion()) {
            conn.setAutoCommit(false);
            try {
                // 1. Actualizar stock del producto
                productoDAO.actualizarStock(conn, idProducto, nuevoStock);

                // 2. Insertar registro de movimiento
                MovimientoInventario mov = new MovimientoInventario();
                mov.setIdProducto(idProducto);
                mov.setIdTipo(idTipo);
                mov.setCantidad(cantidad);
                mov.setMotivo(motivo);

                movimientoDAO.insertar(conn, mov);

                // Confirmar transacción
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new SQLException("Error en la transacción al registrar movimiento: " + e.getMessage(), e);
            } finally {
                conn.setAutoCommit(true);
            }
        }
    }
}

