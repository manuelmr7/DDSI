package Vista;

import Modelo.Actividad;
import Modelo.Socio;
import java.util.List;
/**
 * 
 * @author Manuel Martín Rodrigo
 */

public class VistaActividad {
    /**
     * Menú de las actividades
     */
    public void mostrarMenuActividad() {
        System.out.println("\n**********");
        System.out.println("Actividades");
        System.out.println("**********");
        System.out.println("1. Inscripciones");
        System.out.println("2. Salir");
        System.out.print("Seleccione una opción: ");
    }
    /**
     * Mostramos los socios inscritoss
     * @param socios 
     */
    public void muestraSociosInscritos(List<Socio> socios) {
        System.out.println("Nº Socio\tNombre\tTeléfono\tCorreo");
        for (Socio s : socios) {
            System.out.println(s.getNumeroSocio() + "\t" + s.getNombre() + "\t" + 
                             s.getTelefono() + "\t" + s.getCorreo());
        }
    }
}
