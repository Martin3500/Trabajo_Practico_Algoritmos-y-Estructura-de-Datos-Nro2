package dao;

import config.ConexionBD;
import model.MovimientoInventario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MovimientoInventarioDAO {

    public List<MovimientoInventario> listarTodos() {
        List<MovimientoInventario> lista = new ArrayList<>();
        String sql = """
            SELECT m.id_movimiento, m.id_producto, m.id_tipo, m.cantidad, m.motivo, m.fecha,
                   p.codigo AS producto_codigo, p.nombre AS producto_nombre,
                   t.nombre AS tipo_nombre
            FROM movimientos_inventario m
            INNER JOIN productos p ON m.id_producto = p.id_producto
            INNER JOIN tipos_movimiento t ON m.id_tipo = t.id_tipo
            ORDER BY m.id_movimiento DESC
        """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                MovimientoInventario m = new MovimientoInventario();
                m.setIdMovimiento(rs.getInt("id_movimiento"));
                m.setIdProducto(rs.getInt("id_producto"));
                m.setIdTipo(rs.getInt("id_tipo"));
                m.setCantidad(rs.getInt("cantidad"));
                m.setMotivo(rs.getString("motivo"));
                m.setFecha(rs.getTimestamp("fecha"));
                m.setProductoCodigo(rs.getString("producto_codigo"));
                m.setProductoNombre(rs.getString("producto_nombre"));
                m.setTipoNombre(rs.getString("tipo_nombre"));
                lista.add(m);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar movimientos de inventario: " + e.getMessage());
        }
        return lista;
    }

    public boolean insertar(Connection conn, MovimientoInventario m) throws SQLException {
        String sql = "INSERT INTO movimientos_inventario (id_producto, id_tipo, cantidad, motivo) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, m.getIdProducto());
            ps.setInt(2, m.getIdTipo());
            ps.setInt(3, m.getCantidad());
            ps.setString(4, m.getMotivo());

            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        m.setIdMovimiento(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }
}

