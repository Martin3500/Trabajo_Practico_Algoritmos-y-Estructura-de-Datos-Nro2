package dao;

import config.ConexionBD;
import model.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public List<Usuario> listarTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = """
            SELECT u.id_usuario, u.id_rol, u.nombre, u.username, u.password, u.email, u.fecha_creacion, r.nombre AS rol_nombre
            FROM usuarios u
            INNER JOIN roles r ON u.id_rol = r.id_rol
            ORDER BY u.nombre ASC
        """;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setIdRol(rs.getInt("id_rol"));
                u.setNombre(rs.getString("nombre"));
                u.setUsername(rs.getString("username"));
                u.setPassword(rs.getString("password"));
                u.setEmail(rs.getString("email"));
                u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                u.setRolNombre(rs.getString("rol_nombre"));
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Error al listar usuarios: " + e.getMessage());
        }
        return lista;
    }

    public Usuario obtenerPorId(int idUsuario) {
        String sql = """
            SELECT u.id_usuario, u.id_rol, u.nombre, u.username, u.password, u.email, u.fecha_creacion, r.nombre AS rol_nombre
            FROM usuarios u
            INNER JOIN roles r ON u.id_rol = r.id_rol
            WHERE u.id_usuario = ?
        """;
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setIdRol(rs.getInt("id_rol"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setEmail(rs.getString("email"));
                    u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                    u.setRolNombre(rs.getString("rol_nombre"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Valida las credenciales de inicio de sesión.
     * Retorna el objeto Usuario con su Rol si es válido, o null si falla.
     */
    public Usuario autenticar(String username, String password) {
        String sql = """
            SELECT u.id_usuario, u.id_rol, u.nombre, u.username, u.password, u.email, u.fecha_creacion, r.nombre AS rol_nombre
            FROM usuarios u
            INNER JOIN roles r ON u.id_rol = r.id_rol
            WHERE u.username = ? AND u.password = ?
        """;
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setIdUsuario(rs.getInt("id_usuario"));
                    u.setIdRol(rs.getInt("id_rol"));
                    u.setNombre(rs.getString("nombre"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setEmail(rs.getString("email"));
                    u.setFechaCreacion(rs.getTimestamp("fecha_creacion"));
                    u.setRolNombre(rs.getString("rol_nombre"));
                    return u;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al autenticar usuario: " + e.getMessage());
        }
        return null;
    }

    public boolean insertar(Usuario u) throws SQLException {
        String sql = "INSERT INTO usuarios (id_rol, nombre, username, password, email) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            String pass = (u.getPassword() == null || u.getPassword().trim().isEmpty()) ? "1234" : u.getPassword().trim();

            ps.setInt(1, u.getIdRol());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getUsername());
            ps.setString(4, pass);
            ps.setString(5, u.getEmail());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        u.setIdUsuario(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean actualizar(Usuario u) throws SQLException {
        String sql;
        boolean actualizaPassword = (u.getPassword() != null && !u.getPassword().trim().isEmpty());

        if (actualizaPassword) {
            sql = "UPDATE usuarios SET id_rol = ?, nombre = ?, username = ?, password = ?, email = ? WHERE id_usuario = ?";
        } else {
            sql = "UPDATE usuarios SET id_rol = ?, nombre = ?, username = ?, email = ? WHERE id_usuario = ?";
        }

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, u.getIdRol());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getUsername());
            if (actualizaPassword) {
                ps.setString(4, u.getPassword().trim());
                ps.setString(5, u.getEmail());
                ps.setInt(6, u.getIdUsuario());
            } else {
                ps.setString(4, u.getEmail());
                ps.setInt(5, u.getIdUsuario());
            }
            return ps.executeUpdate() > 0;
        }
    }

    public boolean eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuarios WHERE id_usuario = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;
        }
    }
}
