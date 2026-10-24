package ui;

import model.Categoria;

import javax.swing.*;
import java.awt.*;

public class CategoriaDialog extends JDialog {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtDescripcion = new JTextField(20);

    private boolean guardado = false;
    private final Categoria categoria;
    private final boolean esEdicion;

    public CategoriaDialog(Frame owner, Categoria categoriaAEditar) {
        super(owner, categoriaAEditar == null ? "Nueva Categoría" : "Editar Categoría", true);
        this.categoria = categoriaAEditar != null ? categoriaAEditar : new Categoria();
        this.esEdicion = categoriaAEditar != null;

        initUI();

        if (esEdicion) {
            txtNombre.setText(categoria.getNombre());
            txtDescripcion.setText(categoria.getDescripcion());
        }

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Descripción:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDescripcion, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setForeground(Color.BLACK);
        JButton btnGuardar = new JButton(esEdicion ? "Guardar Cambios" : "Crear Categoría");
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

    private void guardar() {
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre de la categoría es obligatorio.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        categoria.setNombre(nombre);
        categoria.setDescripcion(txtDescripcion.getText().trim());
        guardado = true;
        dispose();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Categoria getCategoria() {
        return categoria;
    }
}

