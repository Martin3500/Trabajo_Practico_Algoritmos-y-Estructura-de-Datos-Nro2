package model;

import java.math.BigDecimal;

public class Producto {
    private int idProducto;
    private int idCategoria;
    private int idUsuario;
    private String codigo;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private int stock;

    // Campos auxiliares para visualización
    private String categoriaNombre;
    private String usuarioNombre;

    public Producto() {
        this.precio = BigDecimal.ZERO;
    }

    public Producto(int idProducto, int idCategoria, int idUsuario, String codigo, String nombre,
                    String descripcion, BigDecimal precio, int stock) {
        this.idProducto = idProducto;
        this.idCategoria = idCategoria;
        this.idUsuario = idUsuario;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getCategoriaNombre() {
        return categoriaNombre;
    }

    public void setCategoriaNombre(String categoriaNombre) {
        this.categoriaNombre = categoriaNombre;
    }

    public String getUsuarioNombre() {
        return usuarioNombre;
    }

    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }

    @Override
    public String toString() {
        return codigo + " - " + nombre + " (Stock: " + stock + ")";
    }
}

