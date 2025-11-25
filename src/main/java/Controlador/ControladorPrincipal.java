package Controlador;

import Vista.VistaInicio;
import Vista.VistaInicioActividades;
import Vista.VistaInicioMonitores;
import Vista.VistaInicioSocios;
import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import org.hibernate.SessionFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorPrincipal implements ActionListener {
    private final SessionFactory sessionFactory;
    private VistaPrincipal vistaPrincipal;
    private VistaMensajes vistaMensajes;
    
    // Paneles
    private VistaInicio vistaInicio;
    private VistaInicioMonitores vistaMonitores;
    private VistaInicioSocios vistaSocios;
    private VistaInicioActividades vistaActividades;

    public ControladorPrincipal(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.vistaPrincipal = new VistaPrincipal();
        this.vistaMensajes = new VistaMensajes();
        
        // Inicializar paneles
        this.vistaInicio = new VistaInicio();
        this.vistaMonitores = new VistaInicioMonitores();
        this.vistaSocios = new VistaInicioSocios();
        this.vistaActividades = new VistaInicioActividades();
        
        // AÑADIR PANELES CON CARDLAYOUT
        vistaPrincipal.panelContenedor.add(vistaInicio, "inicio");
        vistaPrincipal.panelContenedor.add(vistaMonitores, "monitores");
        vistaPrincipal.panelContenedor.add(vistaSocios, "socios");
        vistaPrincipal.panelContenedor.add(vistaActividades, "actividades");
        
        // MOSTRAR PANEL INICIAL
        mostrarPanel("inicio");
        
        addListeners();
        vistaPrincipal.setLocationRelativeTo(null);
        vistaPrincipal.setVisible(true);
    }

    private void mostrarPanel(String nombre) {
        java.awt.CardLayout layout = (java.awt.CardLayout) vistaPrincipal.panelContenedor.getLayout();
        layout.show(vistaPrincipal.panelContenedor, nombre);
        System.out.println("Mostrando panel: " + nombre); // Para verificar
    }

    private void addListeners() {
        // VERIFICA QUE EXISTEN ANTES DE AÑADIR LISTENERS
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
        if (vistaPrincipal.menuItemSalir != null) {
            vistaPrincipal.menuItemSalir.addActionListener(this);
            vistaPrincipal.menuItemSalir.setActionCommand("Salir");
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        System.out.println("ActionCommand recibido: " + e.getActionCommand());
        
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