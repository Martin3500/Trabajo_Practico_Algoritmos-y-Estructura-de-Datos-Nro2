import config.ConexionBD;
import ui.LoginFrame;

import javax.swing.*;

public class App {

    public static void main(String[] args) {
        // Establecer apariencia nativa del sistema y texto negro para botones
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        UIManager.put("Button.foreground", java.awt.Color.BLACK);

        // 1. Inicializar Base de Datos (crea BD, tablas y semillas si no existen)
        System.out.println("==================================================");
        System.out.println("  INICIALIZANDO SISTEMA DE GESTIÓN DE INVENTARIO  ");
        System.out.println("==================================================");

        try {
            ConexionBD.inicializarBaseDeDatos();
            System.out.println("[✓] Base de datos y tablas inicializadas correctamente.");
        } catch (Exception e) {
            System.err.println("[X] Error al inicializar base de datos:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                "No se pudo conectar o inicializar MySQL en XAMPP.\n\n" +
                "Detalle: " + e.getMessage() + "\n\n" +
                "Asegúrate de que el servicio MySQL esté iniciado en el panel de XAMPP.",
                "Error de Conexión a Base de Datos",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        // 2. Iniciar con Pantalla de Inicio de Sesión
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
