package Controlador;

import Modelo.Actividad;
import Modelo.ActividadDAO;
import Modelo.Monitor;
import Util.GestionTablasActividad;
import Vista.VistaInicioActividades;
import Vista.VistaMensajes;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import Modelo.MonitorDAO;
import Vista.VistaActividadDialog;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;

public class ControladorActividad implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInicioActividades vInicioActividades;
    private final ActividadDAO actividadDAO;
    private final VistaMensajes vistaMensajes;
    private final MonitorDAO monitorDAO;
    private Session sesion;

    public ControladorActividad(VistaInicioActividades vInicioActividades, SessionFactory sessionFactory) {
        this.vInicioActividades = vInicioActividades;
        this.sessionFactory = sessionFactory;
        this.actividadDAO = new ActividadDAO();
        this.monitorDAO=monitorDAO();
        this.vistaMensajes = new VistaMensajes();

        addListeners();
        dibujaRellenaTablaActividades();
    }

    private void addListeners() {
        vInicioActividades.nuevaActividad.addActionListener(this);
        vInicioActividades.nuevaActividad.setActionCommand("NuevaActividad");

        vInicioActividades.bajaActividad.addActionListener(this);
        vInicioActividades.bajaActividad.setActionCommand("BajaActividad");

        vInicioActividades.actualizarActividad.addActionListener(this);
        vInicioActividades.actualizarActividad.setActionCommand("ActualizarActividad");
        
        vInicioActividades.verInscripciones.addActionListener(this);
        vInicioActividades.verInscripciones.setActionCommand("VerInscripciones");
    }

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
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "NuevaActividad":
                vistaMensajes.mostrarInfo("Funcionalidad Nueva Actividad en construcción");
                break;
            case "BajaActividad":
                vistaMensajes.mostrarInfo("Funcionalidad Baja Actividad en construcción");
                break;
            case "ActualizarActividad":
                vistaMensajes.mostrarInfo("Funcionalidad Actualizar Actividad en construcción");
                break;
            case "VerInscripciones":
                vistaMensajes.mostrarInfo("Selecciona una actividad para ver sus inscripciones (Próximamente)");
                break;
        }
    }
    private void nuevaActividad()
    {
        VistaActividadDialog dialog=new VistaActividadDialog();
        dialog.setTitle("Nueva Actividad");
        
        cargarDias(dialog);
        cargarMonitores(dialog);
        
        dialog.botonAceptar.addActionListener(evt->insertarActividadEnBD(dialog));
        dialog.botonCancelar.addActionListener(evt->dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    private void insertarActividadEnBD(VistaActividadDialog dialog)
    {
        Actividad a=new Actividad();
        a.setIdActividad(dialog.textoId.getText());
        a.setNombre(dialog.textoNombre.getText());
        a.setDia((String)dialog.comboDia.getSelectedItem());
        a.setDescripcion(dialog.textoDescripcion.getText());
        
        try
        {
            a.setHora(Integer.parseInt(dialog.textoHora.getText()));
            a.setPrecioBaseMes(Integer.parseInt(dialog.textoPrecio.getText()));
        }
        catch(NumberFormatException e)
        {
            vistaMensajes.mostrarError("La hora y el precio deben ser número enteros");
            return;
        }
        Monitor m=obtenerMonitorDelCombo(dialog);
        if(m==null)
        {
            vistaMensajes.mostrarError("Debe seleccionar un monitor válido.");
            return;
        }
        a.setMonitorResponsable(m);
        Transaction tr=null;
        try
        {
            sesion=sessionFactory.openSession();
            tr=sesion.beginTransaction();
            actividadDAO.insertarActividad(sesion, a);
            tr.commit();
            
            vistaMensajes.mostrarInfo("Actividad creada correctamente");
            dialog.dispose();
            dibujaRellenaTablaActividades();
                    
        }
        catch(Exception ex)
        {
            if(tr!=null)
            {
                tr.rollback();
                vistaMensajes.mostrarError("Error al insertar: "+ex.getMessage());
            }
        }
        finally
        {
            if(sesion!=null && sesion.isOpen())
                sesion.close();
        }
        
    }
    private void bajaActividad()
        {
            int fila=vInicioActividades.jTableActividades.getSelectedRow();
            if(fila==-1)
            {
                vistaMensajes.mostrarAdvertencia("Seleccione una actividad para borrar");
                return;
            }
            String id=(String) vInicioActividades.jTableActividades.getValueAt(fila, 0);
            if(JOptionPane.showConfirmDialog(null, "¿Borrar actividad " + id + "?") != JOptionPane.YES_OPTION) return;
            Transaction tr=null;
            try
            {
                sesion=sessionFactory.openSession();
                tr=sesion.beginTransaction();
                Actividad a=actividadDAO.buscarPorId(sesion, id);
                if(a!=null)
                {
                    actividadDAO.borrarActividad(sesion, a);
                    tr.commit();
                    dibujaRellenaTablaActividades();
                }
                
            }
            catch(Exception ex)
            {
                if(tr!=null)
                {
                    tr.rollback();
                    vistaMensajes.mostrarError("Error al borrar"+ex.getMessage());
                }
            }
            finally
            {
                if(sesion!=null && sesion.isOpen())
                    sesion.close();
            }
            
        }
    private void actualizarActividad() throws Exception {
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
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }

        if (a == null) return;

        VistaActividadDialog dialog = new VistaActividadDialog();
        dialog.setTitle("Actualizar Actividad");
        cargarDias(dialog);
        cargarMonitores(dialog);

        // Rellenar datos
        dialog.textoId.setText(a.getIdActividad());
        dialog.textoId.setEditable(false);
        dialog.textoNombre.setText(a.getNombre());
        dialog.textoHora.setText(String.valueOf(a.getHora()));
        dialog.textoPrecio.setText(String.valueOf(a.getPrecioBaseMes()));
        dialog.textoDescripcion.setText(a.getDescripcion());
        dialog.comboDia.setSelectedItem(a.getDia());
        
        // Seleccionar monitor en el combo
        if (a.getMonitorResponsable() != null) {
            String item = a.getMonitorResponsable().getCodMonitor() + " - " + a.getMonitorResponsable().getNombre();
            dialog.comboMonitor.setSelectedItem(item);
        }

        dialog.botonAceptar.setText("Actualizar");
        dialog.botonAceptar.addActionListener(evt -> actualizarActividadEnBD(dialog));
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    private void actualizarActividadEnBD(VistaActividadDialog dialog) {
        // 1. Creamos el objeto Actividad con los datos del formulario
        Actividad a = new Actividad();
        
        // Recogemos los datos de texto (El ID no se edita, pero lo necesitamos para saber cuál actualizar)
        a.setIdActividad(dialog.textoId.getText()); 
        a.setNombre(dialog.textoNombre.getText());
        a.setDia((String) dialog.comboDia.getSelectedItem());
        a.setDescripcion(dialog.textoDescripcion.getText());
        
        // 2. Validación y conversión de números (Hora y Precio)
        try {
            // Asumimos que hora y precio son enteros según tu modelo
            int hora = Integer.parseInt(dialog.textoHora.getText());
            int precio = Integer.parseInt(dialog.textoPrecio.getText());
            a.setHora(hora);
            a.setPrecioBaseMes(precio);
        } catch (NumberFormatException e) {
            vistaMensajes.mostrarError("Error: La hora y el precio deben ser valores numéricos enteros.");
            return; // Salimos del método si hay error, no intentamos guardar
        }

        // 3. Obtener el Monitor seleccionado del ComboBox
        // Usamos el método auxiliar que ya creamos para buscar el objeto Monitor real
        Monitor monitorResponsable = obtenerMonitorDelCombo(dialog);
        
        if (monitorResponsable == null) {
            vistaMensajes.mostrarAdvertencia("Debe seleccionar un Monitor Responsable válido.");
            return;
        }
        
        // Asignamos el monitor a la actividad
        a.setMonitorResponsable(monitorResponsable);

        // 4. Transacción con Hibernate para actualizar en la BD
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            // Llamamos al método del DAO para actualizar
            actividadDAO.actualizarActividad(sesion, a);
            
            tr.commit();
            
            // 5. Feedback al usuario y limpieza
            vistaMensajes.mostrarInfo("Actividad actualizada correctamente.");
            dialog.dispose(); // Cerramos la ventana de diálogo
            dibujaRellenaTablaActividades(); // Refrescamos la tabla principal para ver los cambios
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback(); // Deshacemos si hubo error
            vistaMensajes.mostrarError("Error al actualizar la actividad en la base de datos.\nDetalle: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) {
                sesion.close(); // Siempre cerramos la sesión
            }
        }
    }
    private void cargarDias(VistaActividadDialog dialog)
    {
        String[] dias={"Lunes","Martes","Miércoles","Jueves","Viernes","Sábado","Domingo"};
        dialog.comboDia.setModel(new DefaultComboBoxModel<>(dias));
    }
    private void cargarMonitores(VistaActividadDialog dialog) {
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            // Obtenemos todos los monitores
            List<Monitor> monitores = monitorDAO.listaMonitores(sesion);
            
            DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
            for (Monitor m : monitores) {
                // Guardamos en el combo algo como: "M001 - Pepe Pérez"
                model.addElement(m.getCodMonitor() + " - " + m.getNombre());
            }
            dialog.comboMonitor.setModel(model);
            
        } catch (Exception e) {
            vistaMensajes.mostrarError("Error al cargar monitores");
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }
    private Monitor obtenerMonitorDelCombo(VistaActividadDialog dialog) {
        String seleccionado = (String) dialog.comboMonitor.getSelectedItem();
        if (seleccionado == null) return null;
        
        // Extraemos el código: "M001 - Pepe" -> "M001"
        String codigo = seleccionado.split(" - ")[0];
        
        // Buscamos el objeto real en BD
        Transaction tr = null;
        Monitor m = null;
        try {
            sesion = sessionFactory.openSession();
            m = monitorDAO.buscarPorCodMonitor(sesion, codigo);
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
        return m;
    }
}