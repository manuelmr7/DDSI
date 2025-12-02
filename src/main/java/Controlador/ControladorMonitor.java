/*package Controlador;

import Modelo.Actividad;
import Modelo.Monitor;
import Modelo.MonitorDAO;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.Scanner;

public class ControladorMonitor {
    private final SessionFactory sessionFactory;
    private MonitorDAO monitorDAO;
    private Scanner scanner;
    
    public ControladorMonitor(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.monitorDAO = new MonitorDAO();
        this.scanner = new Scanner(System.in);
        menuMonitor();
    }
    
    private void menuMonitor() {
        int opcion;
        do {
            System.out.println("\n*********");
            System.out.println("Monitores");
            System.out.println("*********");
            System.out.println("1. Actividades de un monitor");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opción: ");
            
            opcion = scanner.nextInt();
            scanner.nextLine();
            
            switch(opcion) {
                case 1:
                    actividadesDeMonitor();
                    break;
                case 2:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        } while(opcion != 2);
    }
    private void actividadesDeMonitor() {
    Session sesion = null;
    try {
        sesion = sessionFactory.openSession();
        
        System.out.print("Introduce el DNI del monitor: ");
        String dniMonitor = scanner.nextLine();
        
        // Verificar que el monitor existe
        Monitor monitor = monitorDAO.buscarPorDni(sesion, dniMonitor);
        if (monitor == null) {
            System.out.println("❌ No existe un monitor con ese DNI");
            return;
        }
        
        // Obtener actividades
        List<Actividad> actividades = monitorDAO.obtenerActividadesPorMonitor(sesion, dniMonitor);
        
        if (actividades.isEmpty()) {
            System.out.println("El monitor no tiene actividades asignadas");
        } else {
            System.out.println("Actividades del monitor " + monitor.getNombre() + ":");
            System.out.println("Nombre\tDía\tHora");
            for (Actividad a : actividades) {
                System.out.println(a.getNombre() + "\t" + a.getDia() + "\t" + a.getHora() + "h");
            }
        }
        
    } catch (Exception e) {
        System.out.println("❌ Error: " + e.getMessage());
    } finally {
        if (sesion != null && sesion.isOpen()) {
            sesion.close();
        }
    }
}
    
}*/

package Controlador;

import Modelo.Monitor;
import Modelo.MonitorDAO;
import Util.GestionTablasMonitor; // Importamos tu clase de utilidad
import Vista.VistaInicioMonitores; // Importamos la vista gráfica
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

// Implementamos ActionListener para manejar botones en el futuro (Diapositiva 15, PDF 1)
public class ControladorMonitor implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInicioMonitores vInicioMonitores; // Referencia a la vista gráfica
    private final MonitorDAO monitorDAO;
    private Session sesion;

    // CONSTRUCTOR
    public ControladorMonitor(VistaInicioMonitores vInicioMonitores, SessionFactory sessionFactory) {
        this.vInicioMonitores = vInicioMonitores;
        this.sessionFactory = sessionFactory;
        this.monitorDAO = new MonitorDAO();
        
        // Inicializamos la tabla nada más arrancar este controlador
        dibujaRellenaTablaMonitores();
        
        // Aquí añadiremos los listeners de los botones más adelante
        // addListeners(); 
    }

    /**
     * Método que configura y rellena la tabla gráfica.
     * Basado en el código de la Diapositiva 6 .
     */
    private void dibujaRellenaTablaMonitores() {
        // 1. Dibujamos la estructura (columnas y anchos)
        GestionTablasMonitor.inicializarTablaMonitores(vInicioMonitores);
        GestionTablasMonitor.dibujarTablaMonitores(vInicioMonitores);
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            // 2. Obtenemos los datos de la base de datos
            List<Monitor> listaMonitores = monitorDAO.listaMonitores(sesion);
            
            // 3. Vaciamos la tabla por si tenía datos viejos
            GestionTablasMonitor.vaciarTablaMonitores();
            
            // 4. Rellenamos con los nuevos datos
            GestionTablasMonitor.rellenarTablaMonitores(listaMonitores);
            
            tr.commit();
            
            // Chivato para consola (puedes borrarlo luego)
            System.out.println("Monitores cargados en tabla: " + listaMonitores.size());

        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            System.out.println("Error al rellenar la tabla de monitores: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Este método se usará más adelante para botones como "Nuevo Monitor", "Salir", etc.
        // Ver Diapositiva 16 del PDF 1.
    }
}