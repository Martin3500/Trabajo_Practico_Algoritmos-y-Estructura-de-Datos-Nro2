package model;

import java.sql.Timestamp;

public class Usuario {
    private int idUsuario;
    private int idRol;
    private String nombre;
    private String username;
    private String password;
    private String email;
    private Timestamp fechaCreacion;
    private String rolNombre; // Para visualización en tablas y vistas

    public Usuario() {}

    public Usuario(int idUsuario, int idRol, String nombre, String username, String password, String email, Timestamp fechaCreacion) {
        this.idUsuario = idUsuario;
        this.idRol = idRol;
        this.nombre = nombre;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fechaCreacion = fechaCreacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdRol() {
        return idRol;
    }

    public void setIdRol(int idRol) {
        this.idRol = idRol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Timestamp fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public boolean esAdmin() {
        if (rolNombre != null && rolNombre.trim().equalsIgnoreCase("Administrador")) {
            return true;
        }
        return idRol == 1;
    }

    @Override
    public String toString() {
        return nombre + " (" + username + ")";
    }
}
