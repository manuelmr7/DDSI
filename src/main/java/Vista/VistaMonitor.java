package Vista;

import Modelo.Monitor;
import java.util.List;
/**
 * 
 * @author Manuel Martín Rodrigo
 */

public class VistaMonitor {
    /**
     * Muestra el menú para los monitores
     */
    public void mostrarMenuMonitor() {
        System.out.println("\n*********");
        System.out.println("Monitores");
        System.out.println("*********");
        System.out.println("1. Actividades de un monitor");
        System.out.println("2. Salir");
        System.out.print("Seleccione una opción: ");
    }
    
    public void mostrarActividadesMonitor(String nombreMonitor, List<String> actividades) {
        System.out.println("Actividades del monitor " + nombreMonitor + ":");
        for (String actividad : actividades) {
            System.out.println("- " + actividad);
        }
    }

    public class jTableMonitores {

        public jTableMonitores() {
        }
    }
}
