package Vista;

import javax.swing.JOptionPane;

/**
 * Clase auxiliar para mostrar mensajes al usuario mediante diálogos (Pop-ups) o consola.
 * @author Manuel Martín Rodrigo
 */
public class VistaMensajes {
    
    /**
     * Imprime un mensaje en la consola del sistema con formato destacado.
     * @param texto Mensaje a imprimir.
     */
    public void mensajeConsola(String texto) {
        System.out.println("***********");
        System.out.println(texto);
        System.out.println("***********");
    }
    
    /**
     * Muestra una ventana emergente con información.
     * @param mensaje Texto informativo.
     */
    public void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Información", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra una ventana emergente de error.
     * @param mensaje Texto del error.
     */
    public void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * Muestra una ventana emergente de advertencia.
     * @param mensaje Texto de advertencia.
     */
    public void mostrarAdvertencia(String mensaje) {
        JOptionPane.showMessageDialog(null, mensaje, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }
}