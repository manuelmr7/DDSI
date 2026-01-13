package Controlador;

import Config.HibernateUtil;
import Vista.VistaConexion;
import Vista.VistaMensajes;
import org.hibernate.SessionFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
/**
 * Controlador de inicio de sesión.
 * Gestiona el establecimiento de la conexión inicial con la base de datos MariaDB
 * utilizando las credenciales introducidas por el usuario.
 *
 * @author manue
 */
public class ControladorConexion implements ActionListener {
    private VistaConexion vistaConexion;
    private VistaMensajes vistaMensajes;
    private SessionFactory sessionFactory;
    private String usuario;
    private String password;

    public ControladorConexion() {
        this.vistaConexion = new VistaConexion();
        this.vistaMensajes = new VistaMensajes();
        
        addListeners();
        
        vistaConexion.pack();
        vistaConexion.setLocationRelativeTo(null);
        vistaConexion.setResizable(false);
        vistaConexion.setVisible(true);
    }

    private void addListeners() {
        vistaConexion.botonEntrar.addActionListener(this);
        vistaConexion.botonCancelar.addActionListener(this);
    }

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

    private void conectar() {
        usuario = vistaConexion.textoUser.getText().trim();
        password = new String(vistaConexion.textoPass.getPassword());

        try {
            sessionFactory = HibernateUtil.buildSessionFactory(usuario, password);
            
            if (sessionFactory == null || sessionFactory.isClosed()) {
                vistaMensajes.mostrarError("Error al introducir las credenciales");
                return;
            }
            
            vistaMensajes.mostrarInfo("Conexión correcta con Hibernate\nVa a acceder a la aplicación");
            vistaConexion.dispose();
            new ControladorPrincipal(sessionFactory);
            
        } catch (Exception ex) {
            HibernateUtil.close();
            vistaMensajes.mostrarError("No se pudo conectar con la base de datos.\nDetalle: " + ex.getMessage());
        }
    }

    private void salir() {
        vistaMensajes.mostrarInfo("Salida correcta de la aplicación");
        vistaConexion.dispose();
        System.exit(0);
    }
}