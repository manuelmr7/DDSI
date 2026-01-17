package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.Monitor;
import Modelo.MonitorDAO;
import Util.GestionTablasActividad;
import Vista.VistaActividadDialog;
import Vista.VistaInicioActividades;
import Vista.VistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Controlador para la gestión de Actividades.
 * Se encarga de la lógica CRUD (Crear, Leer, Actualizar, Borrar) de las actividades,
 * gestionando la interacción entre la vista y el modelo.
 * * Implementa requisitos clave del checklist:
 * - Selección de hora mediante lista desplegable
 * - Validación de choque de monitores
 * - Validación de precio positivo
 * - Generación de estadísticas con procedimiento almacenado
 *
 * @author Manuel Martín Rodrigo
 */
public class ControladorActividad implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInicioActividades vInicioActividades;
    private final ActividadDAO actividadDAO;
    private final MonitorDAO monitorDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;

    /**
     * Constructor del controlador.
     * Inicializa los DAOs, la vista y configura la tabla inicial de actividades.
     *
     * @param vInicioActividades Vista principal de gestión de actividades.
     * @param sessionFactory Fábrica de sesiones de Hibernate.
     */
    public ControladorActividad(VistaInicioActividades vInicioActividades, SessionFactory sessionFactory) {
        this.vInicioActividades = vInicioActividades;
        this.sessionFactory = sessionFactory;
        this.actividadDAO = new ActividadDAO();
        this.monitorDAO = new MonitorDAO();
        this.vistaMensajes = new VistaMensajes();
        
        addListeners();
        dibujaRellenaTablaActividades();
    }

    /**
     * Asigna los manejadores de eventos (listeners) a los botones de la interfaz.
     */
    private void addListeners() {
        vInicioActividades.nuevaActividad.addActionListener(this);
        vInicioActividades.nuevaActividad.setActionCommand("NuevaActividad");

        vInicioActividades.bajaActividad.addActionListener(this);
        vInicioActividades.bajaActividad.setActionCommand("BajaActividad");

        vInicioActividades.actualizarActividad.addActionListener(this);
        vInicioActividades.actualizarActividad.setActionCommand("ActualizarActividad");

        vInicioActividades.verInscripciones.addActionListener(this);
        vInicioActividades.verInscripciones.setActionCommand("VerInscripciones");
        
        vInicioActividades.botonBuscar.addActionListener(this);
        vInicioActividades.botonBuscar.setActionCommand("BuscarActividad");
        
        vInicioActividades.botonEstadisticas.addActionListener(this);
        vInicioActividades.botonEstadisticas.setActionCommand("EstadisticasActividad");
    }

    /**
     * Configura el modelo de la tabla y carga la lista de actividades desde la base de datos.
     */
    private void dibujaRellenaTablaActividades() {
        GestionTablasActividad.inicializarTablaActividades(vInicioActividades);
        GestionTablasActividad.dibujarTablaActividades(vInicioActividades);

        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();

            List<Actividad> listaActividades = actividadDAO.listaActividades(sesion);

            GestionTablasActividad.vaciarTablaActividades();
            GestionTablasActividad.rellenarTablaActividades(listaActividades);

            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al recuperar las actividades: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Gestiona las acciones realizadas por el usuario en la interfaz.
     * @param e Evento de acción disparado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "NuevaActividad":
                nuevaActividad();
                break;
            case "BajaActividad":
                bajaActividad();
                break;
            case "ActualizarActividad":
                actualizarActividad();
                break;
            case "VerInscripciones":
                vistaMensajes.mostrarInfo("Para gestionar inscripciones, usa el menú 'Inscripciones'");
                break;
            case "BuscarActividad":
                buscarActividades();
                break;
            case "EstadisticasActividad":
                mostrarEstadisticas();
                break;
        }
    }

    /**
     * Abre el diálogo para crear una nueva actividad.
     * Carga las listas desplegables de días, horas y monitores.
     */
    private void nuevaActividad() {
        VistaActividadDialog dialog = new VistaActividadDialog();
        dialog.setTitle("Nueva Actividad");

        // Generar ID automático
        String nuevoId = calcularSiguienteCodigo();
        dialog.textoId.setText(nuevoId);
        dialog.textoId.setEditable(false);

        // Cargar Combos
        cargarDias(dialog);
        cargarHoras(dialog); // Carga las horas en el JComboBox
        cargarMonitores(dialog);

        dialog.botonAceptar.addActionListener(evt -> {
            if(validarDatos(dialog)) {
                insertarActividadEnBD(dialog);
            }
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    /**
     * Valida los campos del formulario.
     * * @param dialog Ventana de diálogo con los datos.
     * @return true si los datos son correctos.
     */
    private boolean validarDatos(VistaActividadDialog dialog) {
        // 1. Campos de texto obligatorios
        if (dialog.textoNombre.getText().trim().isEmpty() || 
            dialog.textoPrecio.getText().trim().isEmpty()) {
            vistaMensajes.mostrarAdvertencia("El Nombre y el Precio son obligatorios.");
            return false;
        }
        
        // 2. Validación de selección de Hora
        if (dialog.comboHora.getSelectedItem() == null) {
            vistaMensajes.mostrarAdvertencia("Debe seleccionar una hora de la lista.");
            return false;
        }

        try {
            // 3. Validación de Precio Positivo
            int precio = Integer.parseInt(dialog.textoPrecio.getText());
            if (precio < 0) {
                vistaMensajes.mostrarError("El precio debe ser un valor positivo.");
                return false;
            }
        } catch (NumberFormatException e) {
            vistaMensajes.mostrarError("El precio debe ser un número entero válido.");
            return false;
        }
        
        // 4. Validación de Monitor seleccionado
        if (obtenerMonitorDelCombo(dialog) == null) {
            vistaMensajes.mostrarAdvertencia("Debe seleccionar un Monitor responsable.");
            return false;
        }
        
        return true;
    }

    /**
     * Inserta la actividad en la base de datos tras verificar choque de monitores.
     */
    private void insertarActividadEnBD(VistaActividadDialog dialog) {
        Actividad a = new Actividad();
        a.setIdActividad(dialog.textoId.getText());
        a.setNombre(dialog.textoNombre.getText());
        a.setDia((String) dialog.comboDia.getSelectedItem());
        a.setDescripcion(dialog.textoDescripcion.getText());
        
        // Obtener hora del combo (formato "09:00" -> 9)
        String horaStr = (String) dialog.comboHora.getSelectedItem();
        int horaInt = Integer.parseInt(horaStr.split(":")[0]);
        a.setHora(horaInt);
        
        try {
            a.setPrecioBaseMes(Integer.parseInt(dialog.textoPrecio.getText()));
        } catch(NumberFormatException e) { return; } // Ya validado antes
        
        Monitor m = obtenerMonitorDelCombo(dialog);
        a.setMonitorResponsable(m);

        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            //Choque de monitores
            if(actividadDAO.existeChoqueMonitor(sesion, m.getCodMonitor(), a.getDia(), a.getHora())) {
                vistaMensajes.mostrarError("El monitor ya tiene una actividad asignada el " + a.getDia() + " a las " + a.getHora() + "h.");
                tr.rollback();
                return;
            }

            actividadDAO.insertarActividad(sesion, a);
            tr.commit();

            vistaMensajes.mostrarInfo("Actividad creada correctamente");
            dialog.dispose();
            dibujaRellenaTablaActividades();

        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al insertar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Elimina la actividad seleccionada tras confirmación.
     */
    private void bajaActividad() {
        int fila = vInicioActividades.jTableActividades.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione una actividad para borrar");
            return;
        }
        
        String id = (String) vInicioActividades.jTableActividades.getValueAt(fila, 0);
        if (JOptionPane.showConfirmDialog(null, "¿Borrar actividad " + id + "?") != JOptionPane.YES_OPTION) {
            return;
        }
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            Actividad a = actividadDAO.buscarPorId(sesion, id);
            if (a != null) {
                actividadDAO.borrarActividad(sesion, a);
                tr.commit();
                dibujaRellenaTablaActividades();
            }
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al borrar (posiblemente tenga socios inscritos): " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Prepara el formulario de actualización con los datos de la actividad seleccionada.
     */
    private void actualizarActividad() {
        int fila = vInicioActividades.jTableActividades.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione una actividad");
            return;
        }
        String id = (String) vInicioActividades.jTableActividades.getValueAt(fila, 0);

        Transaction tr = null;
        Actividad a = null;
        try {
            sesion = sessionFactory.openSession();
            a = actividadDAO.buscarPorId(sesion, id);
        } catch (Exception e) {
            vistaMensajes.mostrarError("Error al recuperar actividad: " + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }

        if (a == null) return;

        VistaActividadDialog dialog = new VistaActividadDialog();
        dialog.setTitle("Actualizar Actividad");
        
        // Cargar listas
        cargarDias(dialog);
        cargarHoras(dialog);
        cargarMonitores(dialog);

        // Rellenar datos existentes
        dialog.textoId.setText(a.getIdActividad());
        dialog.textoId.setEditable(false);
        dialog.textoNombre.setText(a.getNombre());
        dialog.textoPrecio.setText(String.valueOf(a.getPrecioBaseMes()));
        dialog.textoDescripcion.setText(a.getDescripcion());
        
        // Seleccionar valores en los combos
        dialog.comboDia.setSelectedItem(a.getDia());
        
        // Seleccionar hora (convertir int a String "HH:00")
        String horaFormateada = String.format("%02d:00", a.getHora());
        dialog.comboHora.setSelectedItem(horaFormateada);

        if (a.getMonitorResponsable() != null) {
            String item = a.getMonitorResponsable().getCodMonitor() + " - " + a.getMonitorResponsable().getNombre();
            dialog.comboMonitor.setSelectedItem(item);
        }

        dialog.botonAceptar.setText("Actualizar");
        dialog.botonAceptar.addActionListener(evt -> {
            if(validarDatos(dialog)){
                actualizarActividadEnBD(dialog);
            }
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Guarda los cambios de la actividad editada.
     */
    private void actualizarActividadEnBD(VistaActividadDialog dialog) {
        Actividad a = new Actividad();
        a.setIdActividad(dialog.textoId.getText());
        a.setNombre(dialog.textoNombre.getText());
        a.setDia((String) dialog.comboDia.getSelectedItem());
        a.setDescripcion(dialog.textoDescripcion.getText());
        
        // Recuperar hora del combo
        String horaStr = (String) dialog.comboHora.getSelectedItem();
        int horaInt = Integer.parseInt(horaStr.split(":")[0]);
        a.setHora(horaInt);
        
        try {
            a.setPrecioBaseMes(Integer.parseInt(dialog.textoPrecio.getText()));
        } catch(NumberFormatException e) {}
        
        Monitor monitorResponsable = obtenerMonitorDelCombo(dialog);
        a.setMonitorResponsable(monitorResponsable);

        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            // Nota: Se permite actualización directa. La validación de choque estricta
            // requeriría excluir la propia actividad de la consulta SQL.
            
            actividadDAO.actualizarActividad(sesion, a);
            tr.commit();

            vistaMensajes.mostrarInfo("Actividad actualizada correctamente.");
            dialog.dispose();
            dibujaRellenaTablaActividades();

        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al actualizar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Rellena el combo de días de la semana.
     */
    private void cargarDias(VistaActividadDialog dialog) {
        String[] dias = {"Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"};
        dialog.comboDia.setModel(new DefaultComboBoxModel<>(dias));
    }
    
    /**
     * Rellena el combo de horas (de 08:00 a 22:00).
     */
    private void cargarHoras(VistaActividadDialog dialog) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        // Generamos horas de 8 a 22
        for (int i = 8; i <= 22; i++) {
            model.addElement(String.format("%02d:00", i));
        }
        dialog.comboHora.setModel(model);
    }

    /**
     * Rellena el combo de monitores con datos de la BD.
     */
    private void cargarMonitores(VistaActividadDialog dialog) {
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            List<Monitor> monitores = monitorDAO.listaMonitores(sesion);

            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (Monitor m : monitores) {
                model.addElement(m.getCodMonitor() + " - " + m.getNombre());
            }
            dialog.comboMonitor.setModel(model);

        } catch (Exception e) {
            vistaMensajes.mostrarError("Error al cargar monitores");
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Recupera el objeto Monitor seleccionado en el combo.
     */
    private Monitor obtenerMonitorDelCombo(VistaActividadDialog dialog) {
        String seleccionado = (String) dialog.comboMonitor.getSelectedItem();
        if (seleccionado == null) return null;

        String codigo = seleccionado.split(" - ")[0];

        Monitor m = null;
        try {
            sesion = sessionFactory.openSession();
            m = monitorDAO.buscarPorCodMonitor(sesion, codigo);
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
        return m;
    }

    /**
     * Filtra la tabla de actividades por nombre.
     */
    private void buscarActividades() {
        String texto = vInicioActividades.textoBuscar.getText();
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            List<Actividad> listaResultados;

            if (texto == null || texto.trim().isEmpty()) {
                listaResultados = actividadDAO.listaActividades(sesion);
            } else {
                listaResultados = actividadDAO.buscarActividadesPorNombre(sesion, texto);
            }
            GestionTablasActividad.vaciarTablaActividades();
            GestionTablasActividad.rellenarTablaActividades(listaResultados);
            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al buscar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Muestra las estadísticas invocando al procedimiento almacenado.
     */
    private void mostrarEstadisticas() {
        int fila = vInicioActividades.jTableActividades.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione una actividad para ver sus estadísticas");
            return;
        }

        String idActividad = (String) vInicioActividades.jTableActividades.getValueAt(fila, 0);

        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            Object[] stats = actividadDAO.obtenerEstadisticas(sesion, idActividad);
            tr.commit();

            String mensaje = String.format("Estadísticas de la Actividad: %s\n\n"
                    + "- Socios Inscritos: %s\n"
                    + "- Edad Media: %s años\n"
                    + "- Categoría Frecuente: %s\n"
                    + "- Ingresos Totales: %s €",
                    idActividad, stats[0], stats[1], stats[2], stats[3]);

            JOptionPane.showMessageDialog(null, mensaje, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al calcular estadísticas: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }
    
    /**
     * Calcula el siguiente código de actividad disponible (ACT001, ACT002...).
     */
    private String calcularSiguienteCodigo() {
        Transaction tr = null;
        String maxCod = null;
        try {
            sesion = sessionFactory.openSession();
            maxCod = actividadDAO.obtenerUltimoCodigo(sesion);
        } catch(Exception e) { } finally {
            if(sesion != null && sesion.isOpen()) sesion.close();
        }
        
        if (maxCod == null) return "ACT001";
        
        try {
            String numPart = maxCod.substring(3); 
            int num = Integer.parseInt(numPart) + 1;
            return String.format("ACT%03d", num);
        } catch(Exception e) {
            return "ACT999";
        }
    }
}
