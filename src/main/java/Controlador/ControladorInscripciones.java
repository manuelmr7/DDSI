package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.Socio;
import Modelo.SocioDAO;
import Vista.VistaInscripciones;
import Vista.VistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Set;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
/**
 * Controlador de la ventana modal de Inscripciones.
 * Gestiona la lógica para matricular a un socio en una actividad o
 * darlo de baja, actualizando las listas visuales en tiempo real.
 *
 * @author manue
 */

public class ControladorInscripciones implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInscripciones vInscripciones;
    private final SocioDAO socioDAO;
    private final ActividadDAO actividadDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;

    public ControladorInscripciones(VistaInscripciones vInscripciones, SessionFactory sessionFactory) {
        this.vInscripciones = vInscripciones;
        this.sessionFactory = sessionFactory;
        this.socioDAO = new SocioDAO();
        this.actividadDAO = new ActividadDAO();
        this.vistaMensajes = new VistaMensajes();

        this.vInscripciones.botonAlta.addActionListener(this);
        this.vInscripciones.botonAlta.setActionCommand("Alta");
        
        this.vInscripciones.botonBaja.addActionListener(this);
        this.vInscripciones.botonBaja.setActionCommand("Baja");
        
        this.vInscripciones.comboSocios.addActionListener(e -> cargarListasActividades());

        cargarSocios();
    }

    private void cargarSocios() {
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            List<Socio> socios = socioDAO.listaSocios(sesion);
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (Socio s : socios) {
                model.addElement(s.getNumeroSocio() + " - " + s.getNombre());
            }
            vInscripciones.comboSocios.setModel(model);
            
            if (model.getSize() > 0) {
                vInscripciones.comboSocios.setSelectedIndex(0);
                cargarListasActividades();
            }
            
        } catch (Exception e) {
            vistaMensajes.mostrarError("Error al cargar socios");
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    private void cargarListasActividades() {
        String seleccionado = (String) vInscripciones.comboSocios.getSelectedItem();
        if (seleccionado == null) return;
        
        String codSocio = seleccionado.split(" - ")[0];
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            
            Socio socio = socioDAO.buscarPorNumeroSocio(sesion, codSocio);
            
            List<Actividad> todas = actividadDAO.listaActividades(sesion);
            
            DefaultListModel<String> modelInscritas = new DefaultListModel<>();
            DefaultListModel<String> modelDisponibles = new DefaultListModel<>();
            
            Set<Actividad> inscritas = socio.getActividadSet();
            
            for (Actividad a : inscritas) {
                modelInscritas.addElement(a.getIdActividad() + " - " + a.getNombre());
            }
            
            for (Actividad a : todas) {
                if (!inscritas.contains(a)) {
                    modelDisponibles.addElement(a.getIdActividad() + " - " + a.getNombre());
                }
            }
            
            vInscripciones.listaActividadesInscritas.setModel(modelInscritas);
            vInscripciones.listaActividadesNoInscritas.setModel(modelDisponibles);
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Alta".equals(e.getActionCommand())) {
            realizarInscripcion();
        } else if ("Baja".equals(e.getActionCommand())) {
            realizarDesinscripcion();
        }
    }

    private void realizarInscripcion() {
        String actSeleccionada = vInscripciones.listaActividadesNoInscritas.getSelectedValue();
        if (actSeleccionada == null) return;
        
        String socioSeleccionado = (String) vInscripciones.comboSocios.getSelectedItem();
        String codSocio = socioSeleccionado.split(" - ")[0];
        String codActividad = actSeleccionada.split(" - ")[0];
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            Socio s = socioDAO.buscarPorNumeroSocio(sesion, codSocio);
            Actividad a = actividadDAO.buscarPorId(sesion, codActividad);
            
            a.agregarSocio(s);
            
            sesion.update(a); 
            tr.commit();
            
            cargarListasActividades();
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error en la inscripción: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    private void realizarDesinscripcion() {
        String actSeleccionada = vInscripciones.listaActividadesInscritas.getSelectedValue();
        if (actSeleccionada == null) return;
        
        String socioSeleccionado = (String) vInscripciones.comboSocios.getSelectedItem();
        String codSocio = socioSeleccionado.split(" - ")[0];
        String codActividad = actSeleccionada.split(" - ")[0];
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            Socio s = socioDAO.buscarPorNumeroSocio(sesion, codSocio);
            Actividad a = actividadDAO.buscarPorId(sesion, codActividad);
            
            a.eliminarSocio(s);
            
            sesion.update(a);
            tr.commit();
            
            cargarListasActividades();
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al desinscribir: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }
}