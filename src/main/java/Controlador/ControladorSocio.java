package Controlador;

import Modelo.Socio;
import Modelo.SocioDAO;
import Util.GestionTablasSocio; // Asegúrate de que esto no tenga tilde
import Vista.VistaInicioSocios;
import Vista.VistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ControladorSocio implements ActionListener {
    
    private final SessionFactory sessionFactory;
    private final VistaInicioSocios vInicioSocios;
    private final SocioDAO socioDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;

    // CONSTRUCTOR
    public ControladorSocio(VistaInicioSocios vInicioSocios, SessionFactory sessionFactory) {
        this.vInicioSocios = vInicioSocios;
        this.sessionFactory = sessionFactory;
        this.socioDAO = new SocioDAO();
        this.vistaMensajes = new VistaMensajes();
        
        // 1. Añadimos listeners a los botones
        addListeners();
        
        // 2. Dibujamos y rellenamos la tabla al iniciar
        dibujaRellenaTablaSocios();
    }
    
    private void addListeners() {
        // Configuramos los ActionCommand para saber qué botón se pulsa
        vInicioSocios.nuevoSocio.addActionListener(this);
        vInicioSocios.nuevoSocio.setActionCommand("NuevoSocio");
        
        vInicioSocios.bajaSocio.addActionListener(this);
        vInicioSocios.bajaSocio.setActionCommand("BajaSocio");
        
        vInicioSocios.actualizarSocio.addActionListener(this);
        vInicioSocios.actualizarSocio.setActionCommand("ActualizarSocio");
    }
    
    private void dibujaRellenaTablaSocios() {
        // Usamos la clase de utilidad para pintar la estructura
        GestionTablasSocio.inicializarTablaSocios(vInicioSocios);
        GestionTablasSocio.dibujarTablaSocios(vInicioSocios);
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            // Usamos el método listaSocios que añadiste al DAO
            List<Socio> listaSocios = socioDAO.listaSocios(sesion);
            
            GestionTablasSocio.vaciarTablaSocios();
            GestionTablasSocio.rellenarTablaSocios(listaSocios);
            
            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al recuperar los socios: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "NuevoSocio":
                // Aquí llamaremos a la ventana de diálogo de Alta (Próximamente)
                vistaMensajes.mostrarInfo("Funcionalidad de Nuevo Socio en construcción");
                break;
            case "BajaSocio":
                // Aquí llamaremos a la lógica de baja
                vistaMensajes.mostrarInfo("Funcionalidad de Baja en construcción");
                break;
            case "ActualizarSocio":
                // Aquí llamaremos a la lógica de actualización
                vistaMensajes.mostrarInfo("Funcionalidad de Actualización en construcción");
                break;
        }
    }
}