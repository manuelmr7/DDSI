package Vista;

import Modelo.Socio;
import java.util.List;

/**
 *
 * @author Manuel Martín Rodrigo
 */

public class VistaSocio {

    /**
     * Muestra el número de un socio y su nombre
     *
     * @author manue
     */
    public void muestraSocios_Numero_Nombre(List<Socio> socios) {
        System.out.println("#Socio\tNombre");
        for (Socio s : socios) {
            System.out.println(s.getNumeroSocio() + "\t" + s.getNombre());
        }
    }

    /**
     * Muestra los socios
     */
    public void mostrarMenuSocios() {
        System.out.println("\n********");
        System.out.println("Socios");
        System.out.println("********");
        System.out.println("1. Alta de un socio");
        System.out.println("2. Salir");
        System.out.print("Seleccione una opción: ");
    }
}
