/*package Controlador;

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

public class ControladorPrincipal implements ActionListener {
    private final SessionFactory sessionFactory;
    private VistaPrincipal vistaPrincipal;
    private VistaMensajes vistaMensajes;
    private VistaInscripciones vistaInscripciones;
    
    // Paneles
    private VistaInicio vistaInicio;
    private VistaInicioMonitores vistaMonitores;
    private VistaInicioSocios vistaSocios;
    private VistaInicioActividades vistaActividades;

    public ControladorPrincipal(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.vistaPrincipal = new VistaPrincipal();
        this.vistaMensajes = new VistaMensajes();
        this.vistaInscripciones = new VistaInscripciones();
        new ControladorInscripciones(this.vistaInscripciones, sessionFactory);
        
        // Inicializar paneles
        this.vistaInicio = new VistaInicio();
        this.vistaMonitores = new VistaInicioMonitores();
        this.vistaSocios = new VistaInicioSocios();
        this.vistaActividades = new VistaInicioActividades();
        //Funcionar tablas
        new ControladorMonitor(this.vistaMonitores,sessionFactory);
        new ControladorSocio(this.vistaSocios,sessionFactory);
        new ControladorActividad(this.vistaActividades,sessionFactory);
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
}*/

package Controlador;

import Vista.VistaInicio;
import Vista.VistaInicioActividades;
import Vista.VistaInicioMonitores;
import Vista.VistaInicioSocios;
import Vista.VistaInscripciones; // Importamos la nueva vista
import Vista.VistaMensajes;
import Vista.VistaPrincipal;
import org.hibernate.SessionFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorPrincipal implements ActionListener {
    
    private final SessionFactory sessionFactory;
    private VistaPrincipal vistaPrincipal;
    private VistaMensajes vistaMensajes;
    
    // Paneles (Vistas)
    private VistaInicio vistaInicio;
    private VistaInicioMonitores vistaMonitores;
    private VistaInicioSocios vistaSocios;
    private VistaInicioActividades vistaActividades;
    private VistaInscripciones vistaInscripciones; // Nuevo panel para inscripciones

    public ControladorPrincipal(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.vistaPrincipal = new VistaPrincipal();
        this.vistaMensajes = new VistaMensajes();
        
        // 1. Inicializar todos los paneles (Vistas)
        this.vistaInicio = new VistaInicio();
        this.vistaMonitores = new VistaInicioMonitores();
        this.vistaSocios = new VistaInicioSocios();
        this.vistaActividades = new VistaInicioActividades();
        this.vistaInscripciones = new VistaInscripciones(); // Inicializamos la vista de inscripciones
        
        // 2. Instanciar los controladores de cada panel (Lógica)
        // Esto conecta cada vista con su lógica y la base de datos
        new ControladorMonitor(this.vistaMonitores, sessionFactory);
        new ControladorSocio(this.vistaSocios, sessionFactory);
        new ControladorActividad(this.vistaActividades, sessionFactory);
        new ControladorInscripciones(this.vistaInscripciones, sessionFactory); // Controlador para inscripciones
        
        // 3. AÑADIR PANELES AL CONTENEDOR PRINCIPAL (CardLayout)
        // La cadena de texto (ej: "monitores") es la clave para mostrarlo después
        vistaPrincipal.panelContenedor.add(vistaInicio, "inicio");
        vistaPrincipal.panelContenedor.add(vistaMonitores, "monitores");
        vistaPrincipal.panelContenedor.add(vistaSocios, "socios");
        vistaPrincipal.panelContenedor.add(vistaActividades, "actividades");
        vistaPrincipal.panelContenedor.add(vistaInscripciones, "inscripciones"); 
        
        // Configuración inicial de la ventana
        mostrarPanel("inicio"); // Mostrar pantalla de inicio por defecto
        addListeners();         // Activar los menús
        
        // Opcional: Maximizar ventana para ver bien las tablas
        // vistaPrincipal.setExtendedState(javax.swing.JFrame.MAXIMIZED_BOTH);
        vistaPrincipal.setLocationRelativeTo(null);
        vistaPrincipal.setVisible(true);
    }

    private void mostrarPanel(String nombre) {
        java.awt.CardLayout layout = (java.awt.CardLayout) vistaPrincipal.panelContenedor.getLayout();
        layout.show(vistaPrincipal.panelContenedor, nombre);
    }

    private void addListeners() {
        // Menú Inicio
        if (vistaPrincipal.menuItemInicio != null) {
            vistaPrincipal.menuItemInicio.addActionListener(this);
            vistaPrincipal.menuItemInicio.setActionCommand("Inicio");
        }
        // Menú Monitores
        if (vistaPrincipal.menuItemGestionMonitores != null) {
            vistaPrincipal.menuItemGestionMonitores.addActionListener(this);
            vistaPrincipal.menuItemGestionMonitores.setActionCommand("GestionMonitores");
        }
        // Menú Socios
        if (vistaPrincipal.menuItemGestionSocios != null) {
            vistaPrincipal.menuItemGestionSocios.addActionListener(this);
            vistaPrincipal.menuItemGestionSocios.setActionCommand("GestionSocios");
        }
        // Menú Actividades
        if (vistaPrincipal.menuItemGestionActividades != null) {
            vistaPrincipal.menuItemGestionActividades.addActionListener(this);
            vistaPrincipal.menuItemGestionActividades.setActionCommand("GestionActividades");
        }
        // NUEVO: Menú Inscripciones (Asegúrate de haberlo creado en VistaPrincipal)
        if (vistaPrincipal.menuItemInscripciones != null) {
            vistaPrincipal.menuItemInscripciones.addActionListener(this);
            vistaPrincipal.menuItemInscripciones.setActionCommand("GestionInscripciones");
        }
        // Menú Salir
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
            case "GestionInscripciones": // Cambio al panel de inscripciones
                mostrarPanel("inscripciones");
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