package Vista;

import javax.swing.JOptionPane;

public class VistaMensajes {
    
    // Método del PDF
    public void mensajeConsola(String texto) {
        System.out.println("***********");
        System.out.println(texto);
        System.out.println("***********");
    }
    
    // Métodos para JOptionPane
    public void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    public void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}