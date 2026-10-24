package ui;

import dao.RolDAO;
import model.Rol;
import model.Usuario;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class UsuarioDialog extends JDialog {

    private final JTextField txtNombre = new JTextField(20);
    private final JTextField txtUsername = new JTextField(20);
    private final JPasswordField txtPassword = new JPasswordField(20);
    private final JTextField txtEmail = new JTextField(20);
    private final JComboBox<Rol> cbRol = new JComboBox<>();

    private boolean guardado = false;
    private final Usuario usuario;
    private final boolean esEdicion;

    public UsuarioDialog(Frame owner, Usuario usuarioAEditar) {
        super(owner, usuarioAEditar == null ? "Nuevo Usuario" : "Editar Usuario", true);
        this.usuario = usuarioAEditar != null ? usuarioAEditar : new Usuario();
        this.esEdicion = usuarioAEditar != null;

        cargarRoles();
        initUI();

        if (esEdicion) {
            cargarDatos();
        } else {
            txtPassword.setText("1234"); // Contraseña por defecto para nuevos usuarios
        }

        pack();
        setLocationRelativeTo(owner);
        setResizable(false);
    }

    private void cargarRoles() {
        RolDAO rolDAO = new RolDAO();
        List<Rol> roles = rolDAO.listarTodos();
        for (Rol r : roles) {
            cbRol.addItem(r);
        }
    }

    private void cargarDatos() {
        txtNombre.setText(usuario.getNombre());
        txtUsername.setText(usuario.getUsername());
        txtPassword.setText(usuario.getPassword());
        txtEmail.setText(usuario.getEmail());

        for (int i = 0; i < cbRol.getItemCount(); i++) {
            Rol r = cbRol.getItemAt(i);
            if (r.getIdRol() == usuario.getIdRol()) {
                cbRol.setSelectedIndex(i);
                break;
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

        // Nombre
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nombre Completo:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtNombre, gbc);

        // Username
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Nombre de Usuario:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtUsername, gbc);

        // Password
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Contraseña:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtPassword, gbc);

        // Email
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Correo Electrónico:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtEmail, gbc);

        // Rol
        row++;
        gbc.gridx = 0; gbc.gridy = row;
        formPanel.add(new JLabel("Rol Asignado:"), gbc);
        gbc.gridx = 1;
        formPanel.add(cbRol, gbc);

        // Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setForeground(Color.BLACK);
        JButton btnGuardar = new JButton(esEdicion ? "Guardar Cambios" : "Crear Usuario");
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
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String email = txtEmail.getText().trim();
        Rol rol = (Rol) cbRol.getSelectedItem();

        if (nombre.isEmpty() || username.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nombre, usuario y correo son obligatorios.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.isEmpty() && !esEdicion) {
            JOptionPane.showMessageDialog(this, "Debe ingresar una contraseña para el nuevo usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (rol == null) {
            JOptionPane.showMessageDialog(this, "Debe seleccionar un rol para el usuario.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        usuario.setNombre(nombre);
        usuario.setUsername(username);
        if (!password.isEmpty()) {
            usuario.setPassword(password);
        }
        usuario.setEmail(email);
        usuario.setIdRol(rol.getIdRol());
        usuario.setRolNombre(rol.getNombre());

        guardado = true;
        dispose();
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Usuario getUsuario() {
        return usuario;
    }
}
