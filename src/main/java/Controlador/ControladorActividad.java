package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.Socio;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import java.util.Scanner;

public class ControladorActividad {
    private final SessionFactory sessionFactory;
    private ActividadDAO actividadDAO;
    private Scanner scanner;
    
    public ControladorActividad(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.actividadDAO = new ActividadDAO();
        this.scanner = new Scanner(System.in);
        menuActividad();
    }
    
    private void menuActividad() {
        int opcion;
        do {
            System.out.println("\n**********");
            System.out.println("Actividades");
            System.out.println("**********");
            System.out.println("1. Inscripciones");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opción: ");
            
            opcion = scanner.nextInt();
            scanner.nextLine();
            
            switch(opcion) {
                case 1:
                    inscripcionesEnActividad();
                    break;
                case 2:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        } while(opcion != 2);
    }
    
    private void inscripcionesEnActividad() {
        Session sesion = null;
        try {
            sesion = sessionFactory.openSession();
            
            System.out.print("Introduce el identificador de la actividad: ");
            String idActividad = scanner.nextLine();
            //Verificar que la actividad existe
            Actividad actividad = actividadDAO.buscarPorId(sesion,idActividad);
            if(actividad == null)
            {
                System.out.println("No existe una activiad con identificador "+idActividad);
            }
            
            //Obtener socios inscritos
            List<Socio> socios=actividadDAO.obtenerSociosInscritos(sesion, idActividad);
            if(socios.isEmpty())
                System.out.println("No hay socios inscritos en esta actividad");
            else
            {
                System.out.println("Socios inscritos en "+actividad.getNombre()+": ");
                System.out.println("Nº Socio\\tNombre\\tTeléfono\\tCorreo");
                for(Socio s: socios)
                {
                    System.out.println(s.getNumeroSocio() + "\t" + s.getNombre() + "\t" + 
                                 s.getTelefono() + "\t" + s.getCorreo());
                }
            }
                
            
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }
}
