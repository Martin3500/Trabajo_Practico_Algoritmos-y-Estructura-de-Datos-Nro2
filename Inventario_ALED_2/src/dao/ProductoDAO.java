package dao;

import config.ConexionBD;
import model.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {

    public List<Producto> listarTodos() {
        List<Producto> lista = new ArrayList<>();
        String sql = """
            SELECT p.id_producto, p.id_categoria, p.id_usuario, p.codigo, p.nombre, p.descripcion, p.precio, p.stock,
                   c.nombre AS categoria_nombre, u.nombre AS usuario_nombre
            FROM productos p
            INNER JOIN categorias c ON p.id_categoria = c.id_categoria
            INNER JOIN usuarios u ON p.id_usuario = u.id_usuario
            ORDER BY p.id_producto DESC
        """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Producto p = new Producto();
                p.setIdProducto(rs.getInt("id_producto"));
                p.setIdCategoria(rs.getInt("id_categoria"));
                p.setIdUsuario(rs.getInt("id_usuario"));
                p.setCodigo(rs.getString("codigo"));
                p.setNombre(rs.getString("nombre"));
                p.setDescripcion(rs.getString("descripcion"));
                p.setPrecio(rs.getBigDecimal("precio"));
                p.setStock(rs.getInt("stock"));
                p.setCategoriaNombre(rs.getString("categoria_nombre"));
                p.setUsuarioNombre(rs.getString("usuario_nombre"));
                lista.add(p);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar productos: " + e.getMessage());
        }
        return lista;
    }

    public Producto obtenerPorId(int idProducto) {
        String sql = """
            SELECT p.id_producto, p.id_categoria, p.id_usuario, p.codigo, p.nombre, p.descripcion, p.precio, p.stock,
                   c.nombre AS categoria_nombre, u.nombre AS usuario_nombre
            FROM productos p
            INNER JOIN categorias c ON p.id_categoria = c.id_categoria
            INNER JOIN usuarios u ON p.id_usuario = u.id_usuario
            WHERE p.id_producto = ?
        """;
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Producto p = new Producto();
                    p.setIdProducto(rs.getInt("id_producto"));
                    p.setIdCategoria(rs.getInt("id_categoria"));
                    p.setIdUsuario(rs.getInt("id_usuario"));
                    p.setCodigo(rs.getString("codigo"));
                    p.setNombre(rs.getString("nombre"));
                    p.setDescripcion(rs.getString("descripcion"));
                    p.setPrecio(rs.getBigDecimal("precio"));
                    p.setStock(rs.getInt("stock"));
                    p.setCategoriaNombre(rs.getString("categoria_nombre"));
                    p.setUsuarioNombre(rs.getString("usuario_nombre"));
                    return p;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener producto por ID: " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(Producto p) throws SQLException {
        String sql = "INSERT INTO productos (id_categoria, id_usuario, codigo, nombre, descripcion, precio, stock) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getIdCategoria());
            ps.setInt(2, p.getIdUsuario());
            ps.setString(3, p.getCodigo());
            ps.setString(4, p.getNombre());
            ps.setString(5, p.getDescripcion());
            ps.setBigDecimal(6, p.getPrecio());
            ps.setInt(7, p.getStock());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        p.setIdProducto(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean actualizar(Producto p) throws SQLException {
        String sql = "UPDATE productos SET id_categoria = ?, id_usuario = ?, codigo = ?, nombre = ?, descripcion = ?, precio = ?, stock = ? WHERE id_producto = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getIdCategoria());
            ps.setInt(2, p.getIdUsuario());
            ps.setString(3, p.getCodigo());
            ps.setString(4, p.getNombre());
            ps.setString(5, p.getDescripcion());
            ps.setBigDecimal(6, p.getPrecio());
            ps.setInt(7, p.getStock());
            ps.setInt(8, p.getIdProducto());
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarStock(Connection conn, int idProducto, int nuevoStock) throws SQLException {
        String sql = "UPDATE productos SET stock = ? WHERE id_producto = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nuevoStock);
            ps.setInt(2, idProducto);
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idProducto) throws SQLException {
        String sql = "DELETE FROM productos WHERE id_producto = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idProducto);
            return ps.executeUpdate() > 0;
        }
    }
}
