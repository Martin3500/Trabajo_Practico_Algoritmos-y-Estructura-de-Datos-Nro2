package ui;

import dao.ProductoDAO;
import dao.TipoMovimientoDAO;
import model.Producto;
import model.TipoMovimiento;
import service.InventarioService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MovimientoDialog extends JDialog {

    private final JComboBox<Producto> cbProducto = new JComboBox<>();
    private final JComboBox<TipoMovimiento> cbTipo = new JComboBox<>();
    private final JLabel lblStockActual = new JLabel("0");
    private final JSpinner spCantidad = new JSpinner(new SpinnerNumberModel(1, 0, 1000000, 1));
    private final JTextField txtMotivo = new JTextField(20);
    private final JLabel lblIndicacion = new JLabel("Indica la cantidad a ingresar o retirar.");

    private boolean completado = false;
    private final InventarioService inventarioService = new InventarioService();

    public MovimientoDialog(Frame owner) {
        super(owner, "Registrar Movimiento de Inventario", true);

        cargarDatos();
        initUI();

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void cargarDatos() {
        ProductoDAO prodDAO = new ProductoDAO();
        List<Producto> productos = prodDAO.listarTodos();
        for (Producto p : productos) {
            cbProducto.addItem(p);
        }

        TipoMovimientoDAO tipoDAO = new TipoMovimientoDAO();
        List<TipoMovimiento> tipos = tipoDAO.listarTodos();
        for (TipoMovimiento t : tipos) {
            cbTipo.addItem(t);
        }

        actualizarInfoProducto();
        actualizarIndicacionTipo();
    }

    private void actualizarInfoProducto() {
        Producto p = (Producto) cbProducto.getSelectedItem();
        if (p != null) {
            lblStockActual.setText(String.valueOf(p.getStock()));
            lblStockActual.setFont(lblStockActual.getFont().deriveFont(Font.BOLD, 14f));
            if (p.getStock() <= 5) {
                lblStockActual.setForeground(new Color(180, 40, 40));
            } else {
                lblStockActual.setForeground(new Color(30, 130, 60));
            }
        }
    }

    private void actualizarIndicacionTipo() {
        TipoMovimiento t = (TipoMovimiento) cbTipo.getSelectedItem();
        if (t != null) {
            String nombre = t.getNombre().toUpperCase();
            if ("ENTRADA".equals(nombre)) {
                lblIndicacion.setText("Se sumará la cantidad ingresada al stock actual.");
                lblIndicacion.setForeground(new Color(30, 130, 60));
            } else if ("SALIDA".equals(nombre)) {
                lblIndicacion.setText("Se restará la cantidad del stock (no puede superar el stock actual).");
                lblIndicacion.setForeground(new Color(180, 40, 40));
            } else if ("AJUSTE".equals(nombre)) {
                lblIndicacion.setText("Se fijará el stock directamente al valor indicado aquí.");
                lblIndicacion.setForeground(new Color(180, 110, 20));
            }
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

        // Producto
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Producto:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbProducto.addActionListener(e -> actualizarInfoProducto());
        formPanel.add(cbProducto, gbc);

        // Stock Actual
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Stock Actual:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(lblStockActual, gbc);

        // Tipo de movimiento
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Tipo de Movimiento:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        cbTipo.addActionListener(e -> actualizarIndicacionTipo());
        formPanel.add(cbTipo, gbc);

        // Indicación
        row++;
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1.0;
        lblIndicacion.setFont(lblIndicacion.getFont().deriveFont(Font.ITALIC, 11f));
        formPanel.add(lblIndicacion, gbc);

        // Cantidad
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Cantidad:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(spCantidad, gbc);

        // Motivo
        row++;
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        formPanel.add(new JLabel("Motivo / Observación:"), gbc);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formPanel.add(txtMotivo, gbc);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setForeground(Color.BLACK);
        JButton btnRegistrar = new JButton("Registrar Movimiento");
        btnRegistrar.setForeground(Color.BLACK);
        btnRegistrar.setFocusPainted(false);

        btnCancelar.addActionListener(e -> dispose());
        btnRegistrar.addActionListener(e -> procesarMovimiento());

        btnPanel.add(btnCancelar);
        btnPanel.add(btnRegistrar);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void procesarMovimiento() {
        Producto prod = (Producto) cbProducto.getSelectedItem();
        TipoMovimiento tipo = (TipoMovimiento) cbTipo.getSelectedItem();

        if (prod == null || tipo == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un producto y un tipo de movimiento.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad = (int) spCantidad.getValue();
        String motivo = txtMotivo.getText().trim();
        if (motivo.isEmpty()) {
            motivo = "Movimiento estándar de " + tipo.getNombre();
        }

        try {
            inventarioService.registrarMovimiento(
                prod.getIdProducto(),
                tipo.getIdTipo(),
                tipo.getNombre(),
                cantidad,
                motivo
            );

            JOptionPane.showMessageDialog(this, "¡Movimiento registrado con éxito! El stock ha sido actualizado.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            completado = true;
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error al registrar el movimiento:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isCompletado() {
        return completado;
    }
}

