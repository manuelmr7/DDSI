package Controlador;

import Modelo.Monitor;
import Modelo.MonitorDAO;
import Util.GestionTablasMonitor;
import Vista.VistaInicioMonitores;
import Vista.VistaMensajes;
import Vista.VistaMonitorDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Controlador para la gestión de Monitores.
 * Se encarga de la lógica CRUD (Crear, Leer, Actualizar, Borrar) de los monitores,
 * gestionando la interacción entre la vista y el modelo de datos.
 * Incluye validaciones estrictas de formatos (DNI, Correo) y fechas.
 *
 * @author Manuel Martín Rodrigo
 */
public class ControladorMonitor implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInicioMonitores vInicioMonitores;
    private final MonitorDAO monitorDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;

    /**
     * Constructor del controlador.
     * Inicializa los componentes, los DAOs y carga la tabla inicial de monitores.
     *
     * @param vInicioMonitores Vista principal de gestión de monitores.
     * @param sessionFactory Fábrica de sesiones de Hibernate para la conexión a BD.
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
     * Asigna los escuchadores de eventos (listeners) a los botones de la vista principal.
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
     * Configura el modelo de la tabla de monitores y refresca los datos desde la base de datos.
     * Gestiona la sesión de Hibernate para realizar la consulta de listado.
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
     * Gestiona las acciones de los botones de la interfaz.
     * @param e Evento de acción disparado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "NuevoMonitor": nuevoMonitor(); break;
            case "BajaMonitor": bajaMonitor(); break;
            case "ActualizarMonitor": actualizarMonitor(); break;
        }
    }

    /**
     * Abre el diálogo para registrar un nuevo monitor.
     * Calcula el siguiente código disponible y prepara el formulario.
     */
    private void nuevoMonitor() {
        VistaMonitorDialog dialog = new VistaMonitorDialog();
        dialog.setTitle("Nuevo Monitor");
        
        String nuevoCodigo = calcularSiguienteCodigo();
        dialog.textoCodigo.setText(nuevoCodigo);
        dialog.textoCodigo.setEditable(false);
        
        dialog.botonAceptar.addActionListener(evt -> {
            if (validarDatos(dialog)) {
                insertarMonitorEnBD(dialog);
            }
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    /**
     * Valida los datos del formulario de Monitor aplicando reglas de negocio.
     * * Validaciones realizadas:
     * 1. Campos obligatorios no vacíos (Nombre, DNI, Correo).
     * 2. Existencia de fecha en el componente JDateChooser.
     * 3. Fecha de Entrada no futura
     * 4. Formato correcto de DNI (8 dígitos + Letra).
     * 5. Formato correcto de Teléfono (9 dígitos).
     * 6. Formato correcto de Correo electrónico.
     * * @param dialog Diálogo que contiene los datos a validar.
     * @return true si todos los datos son válidos, false si hay errores.
     */
    private boolean validarDatos(VistaMonitorDialog dialog) {
        // 1. Campos obligatorios
        if (dialog.textoNombre.getText().trim().isEmpty() || 
            dialog.textoDni.getText().trim().isEmpty() ||
            dialog.textoCorreo.getText().trim().isEmpty()) {
            vistaMensajes.mostrarAdvertencia("Campos obligatorios vacíos.");
            return false;
        }

        // 2. Fecha (Item 21e) usando JDateChooser
        if (dialog.fechaEntradaChooser.getDate() == null) {
            vistaMensajes.mostrarAdvertencia("Seleccione fecha de entrada.");
            return false;
        }
        if (dialog.fechaEntradaChooser.getDate().after(new Date())) {
             vistaMensajes.mostrarError("La fecha de entrada no puede ser futura.");
             return false;
        }

        // 3. Formatos (DNI, Tlf, Correo)
        if (!dialog.textoDni.getText().matches("\\d{8}[A-Z]")) {
            vistaMensajes.mostrarError("DNI incorrecto (8 números + Letra).");
            return false;
        }
        if (!dialog.textoTelefono.getText().matches("\\d{9}")) {
            vistaMensajes.mostrarError("Teléfono inválido (9 dígitos).");
            return false;
        }
        
        String correo=dialog.textoCorreo.getText().trim();
        if (!correo.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,}$")) {
             vistaMensajes.mostrarError("Correo inválido.");
             return false;
        }

        return true;
    }

    /**
     * Inserta un nuevo monitor en la base de datos tras la validación exitosa.
     * Convierte la fecha de JDateChooser a String para almacenarla.
     * @param dialog Diálogo con los datos del nuevo monitor.
     */
    private void insertarMonitorEnBD(VistaMonitorDialog dialog) {
        Monitor m = new Monitor();
        m.setCodMonitor(dialog.textoCodigo.getText());
        m.setNombre(dialog.textoNombre.getText());
        m.setDni(dialog.textoDni.getText());
        m.setTelefono(dialog.textoTelefono.getText());
        m.setCorreo(dialog.textoCorreo.getText());
        m.setNick(dialog.textoNick.getText());
        
        // Convertir Date -> String
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        m.setFechaEntrada(sdf.format(dialog.fechaEntradaChooser.getDate()));
        
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
     * Elimina el monitor seleccionado de la tabla.
     * Solicita confirmación y maneja excepciones si el monitor tiene actividades asignadas.
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
            vistaMensajes.mostrarError("No se puede borrar (posiblemente tenga actividades asignadas).");
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Abre el diálogo de edición cargando los datos del monitor seleccionado.
     * Rellena los campos de texto y el selector de fecha con la información actual.
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
        
        dialog.textoCodigo.setText(m.getCodMonitor());
        dialog.textoCodigo.setEditable(false); 
        dialog.textoNombre.setText(m.getNombre());
        dialog.textoDni.setText(m.getDni());
        dialog.textoTelefono.setText(m.getTelefono());
        dialog.textoCorreo.setText(m.getCorreo());
        dialog.textoNick.setText(m.getNick());
        
        // Cargar fecha en el JDateChooser
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if(m.getFechaEntrada() != null) 
                dialog.fechaEntradaChooser.setDate(sdf.parse(m.getFechaEntrada()));
        } catch(Exception e) {
            // Si hay error en el formato de fecha, se deja el campo vacío
        }
        
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
     * Actualiza la información del monitor en la base de datos con los valores modificados.
     * @param dialog Diálogo con los datos actualizados.
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
            m.setNick(dialog.textoNick.getText());
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            m.setFechaEntrada(sdf.format(dialog.fechaEntradaChooser.getDate()));
            
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
     * Calcula el siguiente código de monitor disponible.
     * Ejemplo: Si el último es M004, devuelve M005.
     * @return String con el nuevo código.
     */
    private String calcularSiguienteCodigo() {
        Transaction tr = null;
        String maxCod = null;
        try {
            sesion = sessionFactory.openSession();
            maxCod = monitorDAO.obtenerUltimoCodigo(sesion);
        } catch(Exception e) { 
            // Ignorar errores puntuales para el cálculo
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
