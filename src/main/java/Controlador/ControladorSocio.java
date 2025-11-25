package Controlador;

import Modelo.Socio;
import Modelo.SocioDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Scanner;

public class ControladorSocio {
    private final SessionFactory sessionFactory;
    private SocioDAO socioDAO;
    private Scanner scanner;
    
    public ControladorSocio(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.socioDAO = new SocioDAO();
        this.scanner = new Scanner(System.in);
        menuSocio();
    }
    
    private void menuSocio() {
        int opcion;
        do {
            System.out.println("\n********");
            System.out.println("Socios");
            System.out.println("********");
            System.out.println("1. Alta de un socio");
            System.out.println("2. Salir");
            System.out.print("Seleccione una opción: ");
            
            opcion = scanner.nextInt();
            scanner.nextLine();
            
            switch(opcion) {
                case 1:
                    altaSocio();
                    break;
                case 2:
                    System.out.println("Volviendo al menú principal...");
                    break;
                default:
                    System.out.println("Opción no válida");
            }
        } while(opcion != 2);
    }
    
    private void altaSocio() {
        Session sesion = null;
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            //Pedir datos únicos para validar
            System.out.println("Introduce el número de socio: ");
            String numeroSocio=scanner.nextLine();
            
            System.out.println("Introduce el DNI: ");
            String dni=scanner.nextLine();
            
            //Validar que no existe socio con el mismo número o DNI
            if(socioDAO.existeSocio(sesion,numeroSocio,dni))
            {
                System.out.println("Error: Socio ya existente");
                return;
            }
                
            
            System.out.println("Introduce el nombre y apellidos: ");
            String nombre=scanner.nextLine();
            
            
            System.out.println("Introduce la fecha de nacimiento (DD/MM/YYYY): ");
            String fecha=scanner.nextLine();
            
            System.out.println("Introduce teléfono:");
            String telefono=scanner.nextLine();
            
            System.out.println("Introduce el correo: ");
            String correo=scanner.nextLine();
            
            System.out.println("Introduce la fecha de entrada (DD/MM/YYYY): ");
            String fechaEntrada=scanner.nextLine();
            
            System.out.println("Introduce la categoría: ");
            String categoriaIn=scanner.nextLine();
            Character categoria=categoriaIn.charAt(0);
            
            //Crear el nuevo socio
            Socio nuevoSocio=new Socio();
            nuevoSocio.setNumeroSocio(numeroSocio);
            nuevoSocio.setDni(dni);
            nuevoSocio.setNombre(nombre);
            nuevoSocio.setFechaNacimiento(fecha);
            nuevoSocio.setTelefono(telefono);
            nuevoSocio.setCorreo(correo);
            nuevoSocio.setFechaEntrada(fechaEntrada);
            nuevoSocio.setCategoria(categoria);
            
            //Insertar en la BD
            socioDAO.insertaSocio(sesion, nuevoSocio);
            tr.commit();
            System.out.println("Socio con número "+ numeroSocio + " y DNI "+ dni + " insertado correctamente en el sistema.");
        } catch (Exception e) {
            if (tr != null) tr.rollback();
            System.out.println("Error: " + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }
}
