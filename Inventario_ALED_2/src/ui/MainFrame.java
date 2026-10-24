package ui;

import dao.CategoriaDAO;
import dao.MovimientoInventarioDAO;
import dao.ProductoDAO;
import dao.UsuarioDAO;
import model.Categoria;
import model.MovimientoInventario;
import model.Producto;
import model.Usuario;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MainFrame extends JFrame {

    // DAOs
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final MovimientoInventarioDAO movimientoDAO = new MovimientoInventarioDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    // Tablas y Modelos
    private JTable tblProductos;
    private DefaultTableModel modeloProductos;
    private TableRowSorter<DefaultTableModel> sorterProductos;

    private JTable tblMovimientos;
    private DefaultTableModel modeloMovimientos;

    private JTable tblCategorias;
    private DefaultTableModel modeloCategorias;

    private JTable tblUsuarios;
    private DefaultTableModel modeloUsuarios;

    // Componentes de búsqueda y estados
    private JTextField txtBuscarProducto;
    private JLabel lblStatus;

    private final Usuario usuarioActual;

    public MainFrame() {
        this(null);
    }

    public MainFrame(Usuario usuarioActual) {
        super("Sistema de Gestión de Inventario (MySQL)");
        this.usuarioActual = (usuarioActual != null) ? usuarioActual : new UsuarioDAO().obtenerPorId(1);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setMinimumSize(new Dimension(950, 550));
        setLocationRelativeTo(null);

        initUI();
        cargarTodosLosDatos();
    }

    private void initUI() {
        JPanel contentPane = new JPanel(new BorderLayout());
        setContentPane(contentPane);

        // 1. Encabezado superior
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(24, 43, 73));
        headerPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Sistema de Gestión de Inventario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);

        JLabel lblSubtitulo = new JLabel("Base de Datos Relacional MySQL (inventario_db)");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitulo.setForeground(new Color(180, 205, 237));

        JPanel headerTextPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        headerTextPanel.setOpaque(false);
        headerTextPanel.add(lblTitulo);
        headerTextPanel.add(lblSubtitulo);

        JLabel lblBadge = new JLabel("  ● Conectado a MySQL  ");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setForeground(new Color(38, 194, 129));
        lblBadge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(38, 194, 129), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));

        // Panel de Usuario en sesión y botón Cerrar Sesión
        JPanel userHeaderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        userHeaderPanel.setOpaque(false);

        String rolTexto = (usuarioActual != null && usuarioActual.getRolNombre() != null) ? usuarioActual.getRolNombre() : "Usuario";
        JLabel lblUserTag = new JLabel("👤 " + (usuarioActual != null ? usuarioActual.getNombre() : "Usuario") + " [" + rolTexto + "]");
        lblUserTag.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUserTag.setForeground((usuarioActual != null && usuarioActual.esAdmin()) ? new Color(255, 215, 0) : Color.WHITE);

        JButton btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnCerrarSesion.setForeground(Color.BLACK);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.addActionListener(e -> {
            int op = JOptionPane.showConfirmDialog(this, "¿Está seguro de cerrar la sesión actual?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION);
            if (op == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
            }
        });

        userHeaderPanel.add(lblBadge);
        userHeaderPanel.add(lblUserTag);
        userHeaderPanel.add(btnCerrarSesion);

        headerPanel.add(headerTextPanel, BorderLayout.WEST);
        headerPanel.add(userHeaderPanel, BorderLayout.EAST);
        contentPane.add(headerPanel, BorderLayout.NORTH);

        // 2. Pestañas principales
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        tabbedPane.addTab("📦 Productos", crearPanelProductos());
        tabbedPane.addTab("🔁 Movimientos de Inventario", crearPanelMovimientos());
        tabbedPane.addTab("🏷️ Categorías", crearPanelCategorias());
        tabbedPane.addTab("👥 Usuarios y Roles", crearPanelUsuarios());

        contentPane.add(tabbedPane, BorderLayout.CENTER);

        // 3. Barra de estado inferior
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(6, 15, 6, 15));
        footerPanel.setBackground(new Color(245, 247, 250));

        lblStatus = new JLabel("Listo.");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblStatus.setForeground(new Color(100, 100, 100));

        footerPanel.add(lblStatus, BorderLayout.WEST);
        contentPane.add(footerPanel, BorderLayout.SOUTH);
    }

    // ==========================================
    // PANEL PRODUCTOS
    // ==========================================
    private JPanel crearPanelProductos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new BorderLayout(10, 10));

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchPanel.add(new JLabel("Buscar producto:"));
        txtBuscarProducto = new JTextField(18);
        searchPanel.add(txtBuscarProducto);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnNuevo = crearBoton("Nuevo Producto", new Color(24, 119, 242));
        JButton btnEditar = estilizarBoton(new JButton("Editar"));
        JButton btnEliminar = estilizarBoton(new JButton("Eliminar"));
        JButton btnRefrescar = estilizarBoton(new JButton("Actualizar"));

        btnNuevo.addActionListener(e -> nuevoProducto());
        btnEditar.addActionListener(e -> editarProducto());
        btnEliminar.addActionListener(e -> eliminarProducto());
        btnRefrescar.addActionListener(e -> cargarProductos());

        actionsPanel.add(btnNuevo);
        actionsPanel.add(btnEditar);
        actionsPanel.add(btnEliminar);
        actionsPanel.add(btnRefrescar);

        toolbar.add(searchPanel, BorderLayout.WEST);
        toolbar.add(actionsPanel, BorderLayout.EAST);

        panel.add(toolbar, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Código", "Nombre", "Categoría", "Usuario Resp.", "Precio ($)", "Stock"};
        modeloProductos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblProductos = new JTable(modeloProductos);
        estilizarTabla(tblProductos);

        // Renderer para stock (destacar bajo stock)
        tblProductos.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                try {
                    int stock = Integer.parseInt(value.toString());
                    if (!isSelected) {
                        if (stock <= 5) {
                            c.setBackground(new Color(255, 230, 230));
                            c.setForeground(new Color(180, 20, 20));
                            setText(stock + " (Bajo)");
                        } else {
                            c.setBackground(Color.WHITE);
                            c.setForeground(new Color(20, 120, 40));
                            setText(String.valueOf(stock));
                        }
                    }
                } catch (Exception ignored) {}
                return c;
            }
        });

        sorterProductos = new TableRowSorter<>(modeloProductos);
        tblProductos.setRowSorter(sorterProductos);

        // Filtro en vivo
        txtBuscarProducto.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void removeUpdate(DocumentEvent e) { filtrar(); }
            @Override
            public void changedUpdate(DocumentEvent e) { filtrar(); }

            private void filtrar() {
                String text = txtBuscarProducto.getText().trim();
                if (text.isEmpty()) {
                    sorterProductos.setRowFilter(null);
                } else {
                    sorterProductos.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }
        });

        panel.add(new JScrollPane(tblProductos), BorderLayout.CENTER);
        return panel;
    }

    private void nuevoProducto() {
        ProductoDialog dialog = new ProductoDialog(this, null, usuarioActual);
        dialog.setVisible(true);
        if (dialog.isGuardado()) {
            try {
                productoDAO.insertar(dialog.getProducto());
                cargarProductos();
                JOptionPane.showMessageDialog(this, "Producto registrado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al guardar producto:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editarProducto() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tblProductos.convertRowIndexToModel(fila);
        int id = (int) modeloProductos.getValueAt(modelRow, 0);

        Producto prod = productoDAO.obtenerPorId(id);
        if (prod != null) {
            ProductoDialog dialog = new ProductoDialog(this, prod);
            dialog.setVisible(true);
            if (dialog.isGuardado()) {
                try {
                    productoDAO.actualizar(dialog.getProducto());
                    cargarProductos();
                    JOptionPane.showMessageDialog(this, "Producto actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al actualizar producto:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void eliminarProducto() {
        int fila = tblProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = tblProductos.convertRowIndexToModel(fila);
        int id = (int) modeloProductos.getValueAt(modelRow, 0);
        String nombre = (String) modeloProductos.getValueAt(modelRow, 2);

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro de eliminar el producto '" + nombre + "'?\n(También se eliminarán sus movimientos históricos asociados)",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                productoDAO.eliminar(id);
                cargarProductos();
                cargarMovimientos();
                JOptionPane.showMessageDialog(this, "Producto eliminado correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al eliminar producto:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cargarProductos() {
        modeloProductos.setRowCount(0);
        List<Producto> lista = productoDAO.listarTodos();
        for (Producto p : lista) {
            modeloProductos.addRow(new Object[]{
                p.getIdProducto(),
                p.getCodigo(),
                p.getNombre(),
                p.getCategoriaNombre(),
                p.getUsuarioNombre(),
                String.format("%.2f", p.getPrecio()),
                p.getStock()
            });
        }
        actualizarStatusBar();
    }

    // ==========================================
    // PANEL MOVIMIENTOS DE INVENTARIO
    // ==========================================
    private JPanel crearPanelMovimientos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        // Toolbar
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnRegistrar = crearBoton("+ Registrar Movimiento", new Color(38, 140, 70));
        JButton btnRefrescar = estilizarBoton(new JButton("Actualizar Historial"));

        btnRegistrar.addActionListener(e -> {
            MovimientoDialog dialog = new MovimientoDialog(this);
            dialog.setVisible(true);
            if (dialog.isCompletado()) {
                cargarMovimientos();
                cargarProductos(); // Refrescar stock en la tabla productos
            }
        });

        btnRefrescar.addActionListener(e -> cargarMovimientos());

        toolbar.add(btnRegistrar);
        toolbar.add(btnRefrescar);
        panel.add(toolbar, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID", "Fecha y Hora", "Código", "Producto", "Tipo Movimiento", "Cantidad", "Motivo"};
        modeloMovimientos = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };

        tblMovimientos = new JTable(modeloMovimientos);
        estilizarTabla(tblMovimientos);

        // Renderer para Tipo de movimiento con color
        tblMovimientos.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected && value != null) {
                    String tipo = value.toString().toUpperCase();
                    if ("ENTRADA".equals(tipo)) {
                        c.setForeground(new Color(20, 130, 40));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else if ("SALIDA".equals(tipo)) {
                        c.setForeground(new Color(200, 30, 30));
                        setFont(getFont().deriveFont(Font.BOLD));
                    } else {
                        c.setForeground(new Color(200, 110, 0));
                        setFont(getFont().deriveFont(Font.BOLD));
                    }
                }
                return c;
            }
        });

        panel.add(new JScrollPane(tblMovimientos), BorderLayout.CENTER);
        return panel;
    }

    private void cargarMovimientos() {
        modeloMovimientos.setRowCount(0);
        List<MovimientoInventario> lista = movimientoDAO.listarTodos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (MovimientoInventario m : lista) {
            modeloMovimientos.addRow(new Object[]{
                m.getIdMovimiento(),
                m.getFecha() != null ? sdf.format(m.getFecha()) : "-",
                m.getProductoCodigo(),
                m.getProductoNombre(),
                m.getTipoNombre(),
                m.getCantidad(),
                m.getMotivo()
            });
        }
        actualizarStatusBar();
    }

    // ==========================================
    // PANEL CATEGORIAS
    // ==========================================
    private JPanel crearPanelCategorias() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnNuevo = crearBoton("Nueva Categoría", new Color(24, 119, 242));
        JButton btnEditar = estilizarBoton(new JButton("Editar"));
        JButton btnEliminar = estilizarBoton(new JButton("Eliminar"));
        JButton btnRefrescar = estilizarBoton(new JButton("Actualizar"));

        btnNuevo.addActionListener(e -> {
            CategoriaDialog dialog = new CategoriaDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isGuardado()) {
                try {
                    categoriaDAO.insertar(dialog.getCategoria());
                    cargarCategorias();
                    cargarProductos();
                    JOptionPane.showMessageDialog(this, "Categoría creada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al crear categoría:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEditar.addActionListener(e -> {
            int fila = tblCategorias.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoría para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) modeloCategorias.getValueAt(fila, 0);
            Categoria cat = categoriaDAO.obtenerPorId(id);
            if (cat != null) {
                CategoriaDialog dialog = new CategoriaDialog(this, cat);
                dialog.setVisible(true);
                if (dialog.isGuardado()) {
                    try {
                        categoriaDAO.actualizar(dialog.getCategoria());
                        cargarCategorias();
                        cargarProductos();
                        JOptionPane.showMessageDialog(this, "Categoría actualizada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error al actualizar categoría:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnEliminar.addActionListener(e -> {
            int fila = tblCategorias.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione una categoría para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) modeloCategorias.getValueAt(fila, 0);
            String nombre = (String) modeloCategorias.getValueAt(fila, 1);

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar la categoría '" + nombre + "'?\nNota: No se puede eliminar si hay productos asociados a ella.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    categoriaDAO.eliminar(id);
                    cargarCategorias();
                    JOptionPane.showMessageDialog(this, "Categoría eliminada con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "No se puede eliminar la categoría porque tiene productos asignados.", "Restricción de Integridad", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRefrescar.addActionListener(e -> cargarCategorias());

        toolbar.add(btnNuevo);
        toolbar.add(btnEditar);
        toolbar.add(btnEliminar);
        toolbar.add(btnRefrescar);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre", "Descripción"};
        modeloCategorias = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblCategorias = new JTable(modeloCategorias);
        estilizarTabla(tblCategorias);

        panel.add(new JScrollPane(tblCategorias), BorderLayout.CENTER);
        return panel;
    }

    private void cargarCategorias() {
        modeloCategorias.setRowCount(0);
        List<Categoria> lista = categoriaDAO.listarTodas();
        for (Categoria c : lista) {
            modeloCategorias.addRow(new Object[]{
                c.getIdCategoria(),
                c.getNombre(),
                c.getDescripcion()
            });
        }
    }

    // ==========================================
    // PANEL USUARIOS Y ROLES
    // ==========================================
    private JPanel crearPanelUsuarios() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(12, 12, 12, 12));

        boolean esAdmin = (usuarioActual != null && usuarioActual.esAdmin());

        JPanel toolbar = new JPanel(new BorderLayout(10, 10));

        JPanel noticePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        if (!esAdmin) {
            JLabel lblRestriccion = new JLabel("  🔒 Solo usuarios con rol Administrador pueden agregar nuevos usuarios.  ");
            lblRestriccion.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lblRestriccion.setForeground(new Color(150, 40, 40));
            lblRestriccion.setBackground(new Color(255, 235, 235));
            lblRestriccion.setOpaque(true);
            lblRestriccion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 180, 180), 1, true),
                new EmptyBorder(5, 8, 5, 8)
            ));
            noticePanel.add(lblRestriccion);
        }

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton btnNuevo = crearBoton("Nuevo Usuario", new Color(24, 119, 242));
        JButton btnEditar = estilizarBoton(new JButton("Editar"));
        JButton btnEliminar = estilizarBoton(new JButton("Eliminar"));
        JButton btnRefrescar = estilizarBoton(new JButton("Actualizar"));

        if (!esAdmin) {
            btnNuevo.setEnabled(false);
            btnNuevo.setToolTipText("Solo los usuarios con rol Administrador pueden registrar nuevos usuarios.");
            btnEditar.setEnabled(false);
            btnEditar.setToolTipText("Solo los usuarios con rol Administrador pueden editar usuarios.");
            btnEliminar.setEnabled(false);
            btnEliminar.setToolTipText("Solo los usuarios con rol Administrador pueden eliminar usuarios.");
        }

        btnNuevo.addActionListener(e -> {
            if (!usuarioActual.esAdmin()) {
                JOptionPane.showMessageDialog(this, "Permiso denegado: Solo los usuarios con rol Administrador pueden agregar nuevos usuarios.", "Acceso Restringido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            UsuarioDialog dialog = new UsuarioDialog(this, null);
            dialog.setVisible(true);
            if (dialog.isGuardado()) {
                try {
                    usuarioDAO.insertar(dialog.getUsuario());
                    cargarUsuarios();
                    JOptionPane.showMessageDialog(this, "Usuario creado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error al crear usuario:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnEditar.addActionListener(e -> {
            if (!usuarioActual.esAdmin()) {
                JOptionPane.showMessageDialog(this, "Permiso denegado: Solo los usuarios con rol Administrador pueden editar usuarios.", "Acceso Restringido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int fila = tblUsuarios.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) modeloUsuarios.getValueAt(fila, 0);
            Usuario u = usuarioDAO.obtenerPorId(id);
            if (u != null) {
                UsuarioDialog dialog = new UsuarioDialog(this, u);
                dialog.setVisible(true);
                if (dialog.isGuardado()) {
                    try {
                        usuarioDAO.actualizar(dialog.getUsuario());
                        cargarUsuarios();
                        cargarProductos();
                        JOptionPane.showMessageDialog(this, "Usuario actualizado exitosamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Error al actualizar usuario:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        btnEliminar.addActionListener(e -> {
            if (!usuarioActual.esAdmin()) {
                JOptionPane.showMessageDialog(this, "Permiso denegado: Solo los usuarios con rol Administrador pueden eliminar usuarios.", "Acceso Restringido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int fila = tblUsuarios.getSelectedRow();
            if (fila == -1) {
                JOptionPane.showMessageDialog(this, "Seleccione un usuario para eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int id = (int) modeloUsuarios.getValueAt(fila, 0);
            String nombre = (String) modeloUsuarios.getValueAt(fila, 1);

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar el usuario '" + nombre + "'?\nNota: No se puede eliminar si es el usuario responsable de algún producto existente.",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    usuarioDAO.eliminar(id);
                    cargarUsuarios();
                    JOptionPane.showMessageDialog(this, "Usuario eliminado con éxito.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "No se puede eliminar el usuario porque tiene productos registrados a su cargo.", "Restricción de Integridad", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnRefrescar.addActionListener(e -> cargarUsuarios());

        actionsPanel.add(btnNuevo);
        actionsPanel.add(btnEditar);
        actionsPanel.add(btnEliminar);
        actionsPanel.add(btnRefrescar);

        toolbar.add(noticePanel, BorderLayout.WEST);
        toolbar.add(actionsPanel, BorderLayout.EAST);
        panel.add(toolbar, BorderLayout.NORTH);

        String[] columnas = {"ID", "Nombre Completo", "Usuario", "Email", "Rol Asignado", "Fecha Registro"};
        modeloUsuarios = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };
        tblUsuarios = new JTable(modeloUsuarios);
        estilizarTabla(tblUsuarios);

        panel.add(new JScrollPane(tblUsuarios), BorderLayout.CENTER);
        return panel;
    }

    private void cargarUsuarios() {
        modeloUsuarios.setRowCount(0);
        List<Usuario> lista = usuarioDAO.listarTodos();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Usuario u : lista) {
            modeloUsuarios.addRow(new Object[]{
                u.getIdUsuario(),
                u.getNombre(),
                u.getUsername(),
                u.getEmail(),
                u.getRolNombre(),
                u.getFechaCreacion() != null ? sdf.format(u.getFechaCreacion()) : "-"
            });
        }
    }

    // ==========================================
    // UTILIDADES GENERALES
    // ==========================================
    private void cargarTodosLosDatos() {
        cargarProductos();
        cargarMovimientos();
        cargarCategorias();
        cargarUsuarios();
    }

    private void actualizarStatusBar() {
        int totalProd = modeloProductos.getRowCount();
        int totalMov = modeloMovimientos.getRowCount();
        lblStatus.setText(String.format("Total Productos: %d  |  Total Movimientos Registrados: %d", totalProd, totalMov));
    }

    private void estilizarTabla(JTable tabla) {
        tabla.setRowHeight(28);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tabla.setShowGrid(true);
        tabla.setGridColor(new Color(230, 233, 238));

        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(240, 243, 246));
        header.setForeground(new Color(50, 50, 50));
        header.setReorderingAllowed(false);
    }

    private JButton crearBoton(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return btn;
    }

    private JButton estilizarBoton(JButton btn) {
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);
        return btn;
    }
}

