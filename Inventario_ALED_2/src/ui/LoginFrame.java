package ui;

import dao.UsuarioDAO;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginFrame extends JFrame {

    private final JTextField txtUsername = new JTextField("admin", 18);
    private final JPasswordField txtPassword = new JPasswordField("admin", 18);
    private final JButton btnIngresar = new JButton("Iniciar Sesión");
    private final JButton btnSalir = new JButton("Salir");
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public LoginFrame() {
        super("Iniciar Sesión - Gestión de Inventario");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initUI();
        pack();
        setLocationRelativeTo(null);
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        // 1. Header
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(24, 43, 73));
        headerPanel.setBorder(new EmptyBorder(25, 25, 20, 25));

        JLabel lblLogo = new JLabel("🔐");
        lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblTitulo = new JLabel("Control de Acceso");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblSub = new JLabel("Sistema de Gestión de Inventario");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSub.setForeground(new Color(180, 205, 237));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(lblLogo);
        headerPanel.add(Box.createVerticalStrut(8));
        headerPanel.add(lblTitulo);
        headerPanel.add(Box.createVerticalStrut(4));
        headerPanel.add(lblSub);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // 2. Formulario
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(20, 30, 15, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 6, 8, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Usuario
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0;
        JLabel lblUser = new JLabel("Usuario:");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtUsername, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        JLabel lblPass = new JLabel("Contraseña:");
        lblPass.setFont(new Font("Segoe UI", Font.BOLD, 13));
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1; gbc.weightx = 1.0;
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        formPanel.add(txtPassword, gbc);

        // Panel de ayuda de credenciales
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel hintPanel = new JPanel();
        hintPanel.setLayout(new BoxLayout(hintPanel, BoxLayout.Y_AXIS));
        hintPanel.setBackground(new Color(245, 247, 250));
        hintPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 225, 235), 1, true),
            new EmptyBorder(8, 10, 8, 10)
        ));

        JLabel lblHintTitle = new JLabel("💡 Cuentas de prueba disponibles:");
        lblHintTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblHintTitle.setForeground(new Color(70, 70, 70));

        JLabel lblHint1 = new JLabel("• Admin:  usuario: admin  |  clave: admin");
        lblHint1.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint1.setForeground(new Color(90, 90, 90));

        JLabel lblHint2 = new JLabel("• Almacén:  usuario: jperez  |  clave: 1234");
        lblHint2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblHint2.setForeground(new Color(90, 90, 90));

        hintPanel.add(lblHintTitle);
        hintPanel.add(Box.createVerticalStrut(3));
        hintPanel.add(lblHint1);
        hintPanel.add(lblHint2);
        formPanel.add(hintPanel, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        // 3. Botones
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 15));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(0, 20, 10, 20));

        btnSalir.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btnSalir.setForeground(Color.BLACK);
        btnSalir.addActionListener(e -> System.exit(0));

        btnIngresar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnIngresar.setForeground(Color.BLACK);
        btnIngresar.setFocusPainted(false);
        btnIngresar.addActionListener(e -> intentarLogin());

        // Permitir presionar ENTER en campos de texto
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    intentarLogin();
                }
            }
        };
        txtUsername.addKeyListener(enterListener);
        txtPassword.addKeyListener(enterListener);

        btnPanel.add(btnSalir);
        btnPanel.add(btnIngresar);

        mainPanel.add(btnPanel, BorderLayout.SOUTH);
        setContentPane(mainPanel);

        // Botón por defecto para ENTER
        getRootPane().setDefaultButton(btnIngresar);
    }

    private void intentarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese su usuario y contraseña.", "Campos Requeridos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Usuario usuarioAutenticado = usuarioDAO.autenticar(username, password);

        if (usuarioAutenticado != null) {
            // Login exitoso
            dispose();
            SwingUtilities.invokeLater(() -> {
                MainFrame mainFrame = new MainFrame(usuarioAutenticado);
                mainFrame.setVisible(true);
            });
        } else {
            JOptionPane.showMessageDialog(
                this,
                "Usuario o contraseña incorrectos.\nPor favor verifique sus credenciales.",
                "Error de Autenticación",
                JOptionPane.ERROR_MESSAGE
            );
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }
}

