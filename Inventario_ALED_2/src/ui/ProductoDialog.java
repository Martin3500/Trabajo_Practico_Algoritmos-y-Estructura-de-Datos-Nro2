package ui;

import dao.CategoriaDAO;
import dao.UsuarioDAO;
import model.Categoria;
import model.Producto;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProductoDialog extends JDialog {

    private final JTextField txtCodigo = new JTextField(15);
    private final JTextField txtNombre = new JTextField(20);
    private final JComboBox<Categoria> cbCategoria = new JComboBox<>();
    private final JComboBox<Usuario> cbUsuario = new JComboBox<>();
    private final JTextField txtPrecio = new JTextField("0.00", 10);
    private final JSpinner spStock = new JSpinner(new SpinnerNumberModel(0, 0, 1000000, 1));
    private final JTextArea txtDescripcion = new JTextArea(3, 20);

    private boolean guardado = false;
    private final Producto producto;
    private final boolean esEdicion;

    public ProductoDialog(Frame owner, Producto productoAEditar) {
        this(owner, productoAEditar, null);
    }

    public ProductoDialog(Frame owner, Producto productoAEditar, Usuario usuarioPorDefecto) {
        super(owner, productoAEditar == null ? "Nuevo Producto" : "Editar Producto", true);
        this.producto = productoAEditar != null ? productoAEditar : new Producto();
        this.esEdicion = productoAEditar != null;

        cargarCombos();
        initUI();

        if (esEdicion) {
            cargarDatos();
        } else if (usuarioPorDefecto != null) {
            for (int i = 0; i < cbUsuario.getItemCount(); i++) {
                Usuario u = cbUsuario.getItemAt(i);
                if (u.getIdUsuario() == usuarioPorDefecto.getIdUsuario()) {
                    cbUsuario.setSelectedIndex(i);
                    break;
                }
            }
        }

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void cargarCombos() {
        CategoriaDAO catDAO = new CategoriaDAO();
        List<Categoria> cats = catDAO.listarTodas();
        for (Categoria c : cats) {
            cbCategoria.addItem(c);
        }

        UsuarioDAO usrDAO = new UsuarioDAO();
        List<Usuario> usrs = usrDAO.listarTodos();
        for (Usuario u : usrs) {
            cbUsuario.addItem(u);
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // Código
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtCodigo, gbc);

        // Nombre
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtNombre, gbc);

        // Categoría
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Categoría:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(cbCategoria, gbc);

        // Usuario Responsable
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Usuario Responsable:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(cbUsuario, gbc);

        // Precio
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Precio ($):"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtPrecio, gbc);

        // Stock
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Stock Inicial:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(spStock, gbc);

        // Descripción
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        formPanel.add(new JScrollPane(txtDescripcion), gbc);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setForeground(Color.BLACK);
        JButton btnGuardar = new JButton(esEdicion ? "Guardar Cambios" : "Crear Producto");
        btnGuardar.setForeground(Color.BLACK);
        btnGuardar.setFocusPainted(false);

        btnCancelar.addActionListener(e -> dispose());
        btnGuardar.addActionListener(e -> guardar());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnGuardar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void cargarDatos() {
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(producto.getPrecio() != null ? producto.getPrecio().toString() : "0.00");
        spStock.setValue(producto.getStock());
        txtDescripcion.setText(producto.getDescripcion());

        for (int i = 0; i < cbCategoria.getItemCount(); i++) {
            Categoria c = cbCategoria.getItemAt(i);
            if (c.getIdCategoria() == producto.getIdCategoria()) {
                cbCategoria.setSelectedIndex(i);
                break;
            }
        }

        for (int i = 0; i < cbUsuario.getItemCount(); i++) {
            Usuario u = cbUsuario.getItemAt(i);
            if (u.getIdUsuario() == producto.getIdUsuario()) {
                cbUsuario.setSelectedIndex(i);
                break;
            }
        }
    }

    private void guardar() {
        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String precioStr = txtPrecio.getText().trim().replace(",", ".");
        Categoria cat = (Categoria) cbCategoria.getSelectedItem();
        Usuario usr = (Usuario) cbUsuario.getSelectedItem();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El código y el nombre son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar una categoría.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (usr == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un usuario responsable.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        BigDecimal precio;
        try {
            precio = new BigDecimal(precioStr);
            if (precio.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "El precio no puede ser negativo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El precio no tiene un formato numérico válido.", "Validación", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int stock = (int) spStock.getValue();

        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setIdCategoria(cat.getIdCategoria());
        producto.setIdUsuario(usr.getIdUsuario());
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setDescripcion(txtDescripcion.getText().trim());

        this.guardado = true;
        dispose();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Producto getProducto() {
        return producto;
    }
}

