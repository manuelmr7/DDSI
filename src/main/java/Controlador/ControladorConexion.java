package Controlador;

import Config.HibernateUtil;
import Vista.VistaConexion;
import Vista.VistaMensajes;
import org.hibernate.SessionFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Controlador para la ventana de inicio de sesión.
 * Gestiona la autenticación del usuario contra la base de datos y la apertura de la ventana principal.
 *
 * @author Manuel Martín Rodrigo
 */
public class ControladorConexion implements ActionListener {
    private VistaConexion vistaConexion;
    private VistaMensajes vistaMensajes;
    private SessionFactory sessionFactory;
    private String usuario;
    private String password;

    /**
     * Constructor del controlador. Inicializa la vista de conexión y sus listeners.
     */
    public ControladorConexion() {
        this.vistaConexion = new VistaConexion();
        this.vistaMensajes = new VistaMensajes();
        
        addListeners();
        
        vistaConexion.pack();
        vistaConexion.setLocationRelativeTo(null);
        vistaConexion.setResizable(false);
        vistaConexion.setVisible(true);
    }

    /**
     * Asigna los listeners a los botones de la vista.
     */
    private void addListeners() {
        vistaConexion.botonEntrar.addActionListener(this);
        vistaConexion.botonCancelar.addActionListener(this);
    }

    /**
     * Maneja los eventos de acción de los botones.
     * @param e Evento disparado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "Entrar":
                conectar();
                break;
            case "Cancelar":
                salir();
                break;
        }
    }

    /**
     * Intenta establecer la conexión con Hibernate usando las credenciales introducidas.
     * Si es exitosa, abre el ControladorPrincipal.
     */
    private void conectar() {
        usuario = vistaConexion.textoUser.getText().trim();
        password = new String(vistaConexion.textoPass.getPassword());

        try {
            sessionFactory = HibernateUtil.buildSessionFactory(usuario, password);
            
            if (sessionFactory == null || sessionFactory.isClosed()) {
                vistaMensajes.mostrarError("Error al introducir las credenciales o conectar con el servidor.");
                return;
            }
            
            vistaMensajes.mostrarInfo("Conexión correcta con Hibernate.\nAccediendo a la aplicación...");
            vistaConexion.dispose();
            new ControladorPrincipal(sessionFactory);
            
        } catch (Exception ex) {
            HibernateUtil.close();
            vistaMensajes.mostrarError("No se pudo conectar con la base de datos.\nDetalle: " + ex.getMessage());
        }
    }

    /**
     * Cierra la aplicación.
     */
    private void salir() {
        vistaMensajes.mostrarInfo("Salida de la aplicación.");
        vistaConexion.dispose();
        System.exit(0);
    }
}