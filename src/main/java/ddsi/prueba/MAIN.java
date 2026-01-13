package ddsi.prueba;

import Controlador.ControladorConexion;
import com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme;
import com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme;
import com.formdev.flatlaf.intellijthemes.FlatNordIJTheme;
/**
 * 
 * @author manue
 */

public class MAIN {

    public static void main(String[] args) {
        try {
            // Opción 1: Arc Dark
            //FlatArcDarkIJTheme.setup();
            
            // Opción 2: Nord
             FlatNordIJTheme.setup();

        } catch (Exception ex) {
            System.err.println("No se pudo iniciar el tema visual");
        }
        new ControladorConexion();
    }
}