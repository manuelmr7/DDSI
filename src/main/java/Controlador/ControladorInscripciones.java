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
 * Controlador para la gestión de Inscripciones.
 * Permite inscribir y desinscribir socios de actividades de forma visual.
 * Gestiona la interacción entre la ventana de inscripciones (Vista) y la base de datos (Modelo).
 * @author Manuel Martín Rodrigo
 */
public class ControladorInscripciones implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInscripciones vInscripciones;
    private final SocioDAO socioDAO;
    private final ActividadDAO actividadDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;

    /**
     * Constructor de la clase.
     * Inicializa los recursos, conecta los listeners y carga la lista inicial de socios.
     * * @param vInscripciones Instancia de la vista (ventana) de inscripciones.
     * @param sessionFactory Fábrica de sesiones para conectar con Hibernate.
     */
    public ControladorInscripciones(VistaInscripciones vInscripciones, SessionFactory sessionFactory) {
        this.vInscripciones = vInscripciones;
        this.sessionFactory = sessionFactory;
        this.socioDAO = new SocioDAO();
        this.actividadDAO = new ActividadDAO();
        this.vistaMensajes = new VistaMensajes();

        addListeners();
        cargarSocios();
    }

    /**
     * Asigna los manejadores de eventos (ActionListeners) a los botones y componentes.
     * Define qué métodos se ejecutan al pulsar Alta, Baja o cambiar de Socio.
     */
    private void addListeners() {
        this.vInscripciones.botonAlta.addActionListener(this);
        this.vInscripciones.botonAlta.setActionCommand("Alta");
        
        this.vInscripciones.botonBaja.addActionListener(this);
        this.vInscripciones.botonBaja.setActionCommand("Baja");
        
        // Listener especial: Al seleccionar otro socio en el desplegable, recargamos sus actividades
        this.vInscripciones.comboSocios.addActionListener(e -> cargarListasActividades());
    }

    /**
     * Recupera todos los socios de la base de datos y los carga en el ComboBox (desplegable).
     * Muestra el ID y Nombre para facilitar la selección.
     */
    private void cargarSocios() {
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            List<Socio> socios = socioDAO.listaSocios(sesion);
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (Socio s : socios) {
                // Formato visual: "S001 - Juan Pérez"
                model.addElement(s.getNumeroSocio() + " - " + s.getNombre());
            }
            vInscripciones.comboSocios.setModel(model);
            
            // Si hay datos, seleccionamos el primero por defecto y actualizamos las listas
            if (model.getSize() > 0) {
                vInscripciones.comboSocios.setSelectedIndex(0);
                cargarListasActividades();
            }
            
        } catch (Exception e) {
            vistaMensajes.mostrarError("Error al cargar socios: " + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Actualiza las dos listas visuales (Inscritas vs Disponibles) según el socio seleccionado.
     * Separa las actividades en las que el socio ya está apuntado de las que no.
     */
    private void cargarListasActividades() {
        String seleccionado = (String) vInscripciones.comboSocios.getSelectedItem();
        if (seleccionado == null) return;
        
        // Extraemos el código del socio del texto del combo (ej: "S001")
        String codSocio = seleccionado.split(" - ")[0];
        
        try {
            sesion = sessionFactory.openSession();
            
            Socio socio = socioDAO.buscarPorNumeroSocio(sesion, codSocio);
            List<Actividad> todas = actividadDAO.listaActividades(sesion);
            
            DefaultListModel<String> modelInscritas = new DefaultListModel<>();
            DefaultListModel<String> modelDisponibles = new DefaultListModel<>();
            
            // Obtenemos el conjunto de actividades donde ya está inscrito
            Set<Actividad> inscritas = socio.getActividadSet(); 
            
            // 1. Rellenar lista de actividades YA INSCRITAS (Derecha)
            for (Actividad a : inscritas) {
                modelInscritas.addElement(a.getIdActividad() + " - " + a.getNombre());
            }
            
            // 2. Rellenar lista de actividades DISPONIBLES (Izquierda)
            // Recorremos todas y añadimos solo las que NO estén en el set de inscritas
            for (Actividad a : todas) {
                boolean yaInscrito = false;
                // Comparamos por ID para asegurar la igualdad
                for(Actividad inscrita : inscritas) {
                    if(inscrita.getIdActividad().equals(a.getIdActividad())) {
                        yaInscrito = true;
                        break;
                    }
                }
                
                if (!yaInscrito) {
                    modelDisponibles.addElement(a.getIdActividad() + " - " + a.getNombre());
                }
            }
            
            // Asignamos los modelos a las listas visuales
            vInscripciones.listaActividadesInscritas.setModel(modelInscritas);
            vInscripciones.listaActividadesNoInscritas.setModel(modelDisponibles);
            
        } catch (Exception e) {
            vistaMensajes.mostrarError("Error al cargar listas: " + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Gestiona los clics en los botones "Alta" (Inscribir) y "Baja" (Desinscribir).
     * @param e Evento de acción disparado por el botón.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if ("Alta".equals(e.getActionCommand())) {
            realizarInscripcion();
        } else if ("Baja".equals(e.getActionCommand())) {
            realizarDesinscripcion();
        }
    }

    /**
     * Realiza la inscripción de un socio en una actividad.
     * Mueve la actividad de la lista de disponibles a la de inscritas y guarda en BD.
     */
    private void realizarInscripcion() {
        String actSeleccionada = vInscripciones.listaActividadesNoInscritas.getSelectedValue();
        
        // Validación visual: debe haber algo seleccionado
        if (actSeleccionada == null) {
            vistaMensajes.mostrarAdvertencia("Seleccione una actividad de la izquierda (Disponibles).");
            return;
        }
        
        String socioSeleccionado = (String) vInscripciones.comboSocios.getSelectedItem();
        String codSocio = socioSeleccionado.split(" - ")[0];
        String codActividad = actSeleccionada.split(" - ")[0];
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            Socio s = socioDAO.buscarPorNumeroSocio(sesion, codSocio);
            Actividad a = actividadDAO.buscarPorId(sesion, codActividad);
            
            // Usamos el método helper de la entidad Actividad para mantener la consistencia
            a.agregarSocio(s);
            
            // Actualizamos ambas entidades
            sesion.update(a); 
            sesion.update(s);
            
            tr.commit();
            
            vistaMensajes.mostrarInfo("Inscripción realizada correctamente.");
            // Refrescamos las listas para reflejar el cambio
            cargarListasActividades();
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al inscribir: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Da de baja a un socio de una actividad.
     * Mueve la actividad de la lista de inscritas a la de disponibles y actualiza la BD.
     */
    private void realizarDesinscripcion() {
        String actSeleccionada = vInscripciones.listaActividadesInscritas.getSelectedValue();
        
        // Validación visual
        if (actSeleccionada == null) {
            vistaMensajes.mostrarAdvertencia("Seleccione una actividad de la derecha (Inscritas).");
            return;
        }
        
        String socioSeleccionado = (String) vInscripciones.comboSocios.getSelectedItem();
        String codSocio = socioSeleccionado.split(" - ")[0];
        String codActividad = actSeleccionada.split(" - ")[0];
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            Socio s = socioDAO.buscarPorNumeroSocio(sesion, codSocio);
            Actividad a = actividadDAO.buscarPorId(sesion, codActividad);
            
            // Usamos el método helper para eliminar la relación
            a.eliminarSocio(s);
            
            sesion.update(a);
            sesion.update(s);
            
            tr.commit();
            
            vistaMensajes.mostrarInfo("Baja realizada correctamente.");
            cargarListasActividades();
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al dar de baja: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }
}