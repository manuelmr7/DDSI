package Controlador;

import Modelo.Monitor;
import Modelo.MonitorDAO;
import Util.GestionTablasMonitor;
import Vista.VistaInicioMonitores;
import Vista.VistaMensajes;
import Vista.VistaMonitorDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Controlador para la gestión de Monitores.
 * Se encarga de la lógica CRUD (Crear, Leer, Actualizar, Borrar) de los monitores,
 * gestionando la interacción entre la VistaInicioMonitores y el modelo de datos.
 * * @author Manuel Martín Rodrigo
 */
public class ControladorMonitor implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInicioMonitores vInicioMonitores;
    private final MonitorDAO monitorDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;

    /**
     * Constructor del controlador.
     * Inicializa las referencias a la vista, el DAO y los mensajes, y configura la tabla inicial.
     * * @param vInicioMonitores Instancia de la vista principal de monitores.
     * @param sessionFactory Fábrica de sesiones de Hibernate compartida.
     */
    public ControladorMonitor(VistaInicioMonitores vInicioMonitores, SessionFactory sessionFactory) {
        this.vInicioMonitores = vInicioMonitores;
        this.sessionFactory = sessionFactory;
        this.monitorDAO = new MonitorDAO();
        this.vistaMensajes = new VistaMensajes();
        
        addListeners();
        dibujaRellenaTablaMonitores();
    }
    
    /**
     * Asigna los manejadores de eventos (listeners) a los botones de la vista.
     */
    private void addListeners() {
        vInicioMonitores.nuevoMonitor.addActionListener(this);
        vInicioMonitores.nuevoMonitor.setActionCommand("NuevoMonitor");
        
        vInicioMonitores.bajaMonitor.addActionListener(this);
        vInicioMonitores.bajaMonitor.setActionCommand("BajaMonitor");
        
        vInicioMonitores.actualizaciónMonitor.addActionListener(this);
        vInicioMonitores.actualizaciónMonitor.setActionCommand("ActualizarMonitor");
    }

    /**
     * Configura el modelo de la tabla visual y carga la lista de monitores desde la base de datos.
     * Realiza una transacción de lectura mediante Hibernate.
     */
    private void dibujaRellenaTablaMonitores() {
        GestionTablasMonitor.inicializarTablaMonitores(vInicioMonitores);
        GestionTablasMonitor.dibujarTablaMonitores(vInicioMonitores);
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            List<Monitor> listaMonitores = monitorDAO.listaMonitores(sesion);
            GestionTablasMonitor.vaciarTablaMonitores();
            GestionTablasMonitor.rellenarTablaMonitores(listaMonitores);
            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al recuperar monitores: " + ex.getMessage());
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
            case "NuevoMonitor":
                nuevoMonitor();
                break;
            case "BajaMonitor":
                bajaMonitor();
                break;
            case "ActualizarMonitor":
                actualizarMonitor();
                break;
        }
    }

    /**
     * Prepara y muestra la ventana de diálogo para dar de alta un nuevo monitor.
     * Calcula automáticamente el siguiente código disponible.
     */
    private void nuevoMonitor() {
        VistaMonitorDialog dialog = new VistaMonitorDialog();
        dialog.setTitle("Nuevo Monitor");
        
        String nuevoCodigo = calcularSiguienteCodigo();
        dialog.textoCodigo.setText(nuevoCodigo);
        dialog.textoCodigo.setEditable(false);
        
        dialog.botonAceptar.addActionListener(evt -> {
            // Validamos antes de intentar guardar
            if (validarDatos(dialog)) {
                insertarMonitorEnBD(dialog);
            }
        });
        
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    /**
     * Valida los datos introducidos en el formulario de Monitor.
     * * Validaciones incluidas:
     * - Campos obligatorios vacíos.
     * - Formato de DNI: 8 dígitos + Letra Mayúscula.
     * - Formato de Correo: Patrón estándar email.
     * - Longitud de Teléfono: 9 dígitos.
     * - Fecha de Entrada: No puede ser futura.
     * * @param dialog Diálogo que contiene los campos de texto a validar.
     * @return true si todos los datos son válidos, false si hay algún error.
     */
    private boolean validarDatos(VistaMonitorDialog dialog) {
        // 1. Campos obligatorios
        if (dialog.textoNombre.getText().trim().isEmpty() || 
            dialog.textoDni.getText().trim().isEmpty() ||
            dialog.textoCorreo.getText().trim().isEmpty()) {
            vistaMensajes.mostrarAdvertencia("Existen campos obligatorios vacíos (Nombre, DNI o Correo).");
            return false;
        }

        // 2. Validación DNI 
        if (!dialog.textoDni.getText().matches("\\d{8}[A-Z]")) {
            vistaMensajes.mostrarError("DNI inválido. Formato requerido: 8 números y 1 letra mayúscula (Ej: 12345678Z).");
            return false;
        }

        // 3. Validación Teléfono
        if (!dialog.textoTelefono.getText().matches("\\d{9}")) {
            vistaMensajes.mostrarError("Teléfono inválido. Debe contener exactamente 9 dígitos numéricos.");
            return false;
        }
        
        // 4. Validación Correo
        if (!dialog.textoCorreo.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
             vistaMensajes.mostrarError("Formato de correo inválido (ejemplo: usuario@dominio.com).");
             return false;
        }
        
        // 5. Validación Fecha Entrada
        try {
            java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
            java.time.LocalDate fecha = java.time.LocalDate.parse(dialog.textoFecha.getText(), fmt);
            if (fecha.isAfter(java.time.LocalDate.now())) {
                vistaMensajes.mostrarError("La fecha de entrada no puede ser posterior a la fecha actual.");
                return false;
            }
        } catch (Exception e) {
            vistaMensajes.mostrarError("Formato de fecha incorrecto. Utilice DD/MM/AAAA.");
            return false;
        }

        return true;
    }

    /**
     * Recoge los datos validados del diálogo e inserta el nuevo monitor en la BD.
     * * @param dialog Diálogo con los datos del formulario.
     */
    private void insertarMonitorEnBD(VistaMonitorDialog dialog) {
        Monitor m = new Monitor();
        m.setCodMonitor(dialog.textoCodigo.getText());
        m.setNombre(dialog.textoNombre.getText());
        m.setDni(dialog.textoDni.getText());
        m.setTelefono(dialog.textoTelefono.getText());
        m.setCorreo(dialog.textoCorreo.getText());
        m.setFechaEntrada(dialog.textoFecha.getText());
        m.setNick(dialog.textoNick.getText());
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            monitorDAO.insertarMonitor(sesion, m);
            tr.commit();
            
            vistaMensajes.mostrarInfo("Monitor insertado correctamente");
            dialog.dispose();
            dibujaRellenaTablaMonitores(); 
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al insertar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Elimina el monitor seleccionado en la tabla, previa confirmación.
     * Controla excepciones de integridad referencial (si tiene actividades asignadas).
     */
    private void bajaMonitor() {
        int fila = vInicioMonitores.jTableMonitores.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione un monitor para borrar");
            return;
        }
        
        String codigo = (String) vInicioMonitores.jTableMonitores.getValueAt(fila, 0);
        
        int opt = JOptionPane.showConfirmDialog(null, "¿Seguro que quieres borrar al monitor " + codigo + "?");
        if (opt != JOptionPane.YES_OPTION) return;

        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            Monitor m = monitorDAO.buscarPorCodMonitor(sesion, codigo);
            if (m != null) {
                monitorDAO.borrarMonitor(sesion, m);
                tr.commit();
                dibujaRellenaTablaMonitores();
            }
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("No se puede borrar (posiblemente tenga actividades asignadas): " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Abre el diálogo de edición cargando los datos del monitor seleccionado.
     */
    private void actualizarMonitor() {
        int fila = vInicioMonitores.jTableMonitores.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione un monitor para actualizar");
            return;
        }
        
        String codigo = (String) vInicioMonitores.jTableMonitores.getValueAt(fila, 0);
        
        Transaction tr = null;
        Monitor m = null;
        try {
            sesion = sessionFactory.openSession();
            m = monitorDAO.buscarPorCodMonitor(sesion, codigo);
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
        
        if (m == null) return;

        VistaMonitorDialog dialog = new VistaMonitorDialog();
        dialog.setTitle("Actualizar Monitor");
        
        // Rellenar campos
        dialog.textoCodigo.setText(m.getCodMonitor());
        dialog.textoCodigo.setEditable(false); 
        dialog.textoNombre.setText(m.getNombre());
        dialog.textoDni.setText(m.getDni());
        dialog.textoTelefono.setText(m.getTelefono());
        dialog.textoCorreo.setText(m.getCorreo());
        dialog.textoFecha.setText(m.getFechaEntrada());
        dialog.textoNick.setText(m.getNick());
        
        dialog.botonAceptar.setText("Actualizar");
        
        dialog.botonAceptar.addActionListener(evt -> {
            if (validarDatos(dialog)) {
                actualizarMonitorEnBD(dialog);
            }
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Guarda los cambios realizados sobre un monitor existente en la base de datos.
     * @param dialog Diálogo con los datos modificados.
     */
    private void actualizarMonitorEnBD(VistaMonitorDialog dialog) {
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            Monitor m = new Monitor();
            m.setCodMonitor(dialog.textoCodigo.getText());
            m.setNombre(dialog.textoNombre.getText());
            m.setDni(dialog.textoDni.getText());
            m.setTelefono(dialog.textoTelefono.getText());
            m.setCorreo(dialog.textoCorreo.getText());
            m.setFechaEntrada(dialog.textoFecha.getText());
            m.setNick(dialog.textoNick.getText());
            
            monitorDAO.actualizarMonitor(sesion, m);
            tr.commit();
            
            vistaMensajes.mostrarInfo("Monitor actualizado correctamente");
            dialog.dispose();
            dibujaRellenaTablaMonitores();
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al actualizar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Calcula el siguiente código de monitor disponible (Formato M001, M002...).
     * @return String con el nuevo código.
     */
    private String calcularSiguienteCodigo() {
        Transaction tr = null;
        String maxCod = null;
        try {
            sesion = sessionFactory.openSession();
            // Requiere que MonitorDAO tenga este método implementado
            maxCod = monitorDAO.obtenerUltimoCodigo(sesion);
        } catch(Exception e) {
            // Si falla o no existe, asumimos nulo
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
        
        if (maxCod == null || maxCod.isEmpty()) return "M001";
        
        try {
            String numPart = maxCod.substring(1);
            int num = Integer.parseInt(numPart) + 1;
            return String.format("M%03d", num);
        } catch (Exception e) {
            return "M999"; 
        }
    }
}
