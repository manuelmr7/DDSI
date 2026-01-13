package Controlador;

import Vista.VistaInicio;
import Vista.VistaInicioActividades;
import Vista.VistaInicioMonitores;
import Vista.VistaInicioSocios;
import Vista.VistaInscripciones;
import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import org.hibernate.SessionFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
/**
 * Controlador de la ventana principal de la aplicación.
 * Gestiona la navegación por el menú y la apertura de las distintas
 * secciones (Socios, Monitores, Actividades) y ventanas modales.
 *
 * @author manue
 */
public class ControladorPrincipal implements ActionListener {

    private final SessionFactory sessionFactory;
    private VistaPrincipal vistaPrincipal;
    private VistaMensajes vistaMensajes;
    private VistaInicio vistaInicio;
    private VistaInicioMonitores vistaMonitores;
    private VistaInicioSocios vistaSocios;
    private VistaInicioActividades vistaActividades;
    private VistaInscripciones vistaInscripciones;

    public ControladorPrincipal(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.vistaPrincipal = new VistaPrincipal();
        this.vistaMensajes = new VistaMensajes();
        this.vistaInicio = new VistaInicio();
        this.vistaMonitores = new VistaInicioMonitores();
        this.vistaSocios = new VistaInicioSocios();
        this.vistaActividades = new VistaInicioActividades();
        this.vistaInscripciones = new VistaInscripciones();
        new ControladorMonitor(this.vistaMonitores, sessionFactory);
        new ControladorSocio(this.vistaSocios, sessionFactory);
        new ControladorActividad(this.vistaActividades, sessionFactory);
        new ControladorInscripciones(this.vistaInscripciones, sessionFactory);
        vistaPrincipal.panelContenedor.add(vistaInicio, "inicio");
        vistaPrincipal.panelContenedor.add(vistaMonitores, "monitores");
        vistaPrincipal.panelContenedor.add(vistaSocios, "socios");
        vistaPrincipal.panelContenedor.add(vistaActividades, "actividades");
        mostrarPanel("inicio");
        addListeners();
        vistaPrincipal.setLocationRelativeTo(null);
        vistaPrincipal.setVisible(true);
    }

    private void mostrarPanel(String nombre) {
        java.awt.CardLayout layout = (java.awt.CardLayout) vistaPrincipal.panelContenedor.getLayout();
        layout.show(vistaPrincipal.panelContenedor, nombre);
    }

    private void addListeners() {
        if (vistaPrincipal.menuItemInicio != null) {
            vistaPrincipal.menuItemInicio.addActionListener(this);
            vistaPrincipal.menuItemInicio.setActionCommand("Inicio");
        }
        if (vistaPrincipal.menuItemGestionMonitores != null) {
            vistaPrincipal.menuItemGestionMonitores.addActionListener(this);
            vistaPrincipal.menuItemGestionMonitores.setActionCommand("GestionMonitores");
        }
        if (vistaPrincipal.menuItemGestionSocios != null) {
            vistaPrincipal.menuItemGestionSocios.addActionListener(this);
            vistaPrincipal.menuItemGestionSocios.setActionCommand("GestionSocios");
        }
        if (vistaPrincipal.menuItemGestionActividades != null) {
            vistaPrincipal.menuItemGestionActividades.addActionListener(this);
            vistaPrincipal.menuItemGestionActividades.setActionCommand("GestionActividades");
        }
        if (vistaPrincipal.menuItemInscripciones != null) {
            vistaPrincipal.menuItemInscripciones.addActionListener(this);
            vistaPrincipal.menuItemInscripciones.setActionCommand("GestionInscripciones");
        }
        if (vistaPrincipal.menuItemSalir != null) {
            vistaPrincipal.menuItemSalir.addActionListener(this);
            vistaPrincipal.menuItemSalir.setActionCommand("Salir");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Inicio":
                mostrarPanel("inicio");
                break;
            case "GestionMonitores":
                mostrarPanel("monitores");
                break;
            case "GestionSocios":
                mostrarPanel("socios");
                break;
            case "GestionActividades":
                mostrarPanel("actividades");
                break;
            case "GestionInscripciones":
                javax.swing.JDialog dialogInscripciones = new javax.swing.JDialog(vistaPrincipal, "Gestión de Inscripciones", true);
                Vista.VistaInscripciones panelInscripciones = new Vista.VistaInscripciones();
                new ControladorInscripciones(panelInscripciones, sessionFactory);
                dialogInscripciones.add(panelInscripciones);
                dialogInscripciones.pack();
                dialogInscripciones.setLocationRelativeTo(null);
                dialogInscripciones.setVisible(true);
            break;
            case "Salir":
                salir();
                break;
        }
    }

    private void salir() {
        vistaMensajes.mostrarInfo("¡Hasta pronto!");
        vistaPrincipal.dispose();
        sessionFactory.close();
        System.exit(0);
    }
}
