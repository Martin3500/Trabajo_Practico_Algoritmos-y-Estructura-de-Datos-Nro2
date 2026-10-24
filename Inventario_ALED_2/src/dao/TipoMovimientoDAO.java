package dao;

import config.ConexionBD;
import model.TipoMovimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TipoMovimientoDAO {

    public List<TipoMovimiento> listarTodos() {
        List<TipoMovimiento> lista = new ArrayList<>();
        String sql = "SELECT id_tipo, nombre, descripcion FROM tipos_movimiento ORDER BY id_tipo ASC";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TipoMovimiento tipo = new TipoMovimiento(
                    rs.getInt("id_tipo"),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                );
                lista.add(tipo);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar tipos de movimiento: " + e.getMessage());
        }
        return lista;
    }

    public TipoMovimiento obtenerPorId(int idTipo) {
        String sql = "SELECT id_tipo, nombre, descripcion FROM tipos_movimiento WHERE id_tipo = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idTipo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new TipoMovimiento(
                        rs.getInt("id_tipo"),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener tipo de movimiento por ID: " + e.getMessage());
        }
        return null;
    }
}

