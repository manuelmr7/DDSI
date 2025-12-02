package Controlador;

import Modelo.Monitor;
import Modelo.MonitorDAO;
import Util.GestionTablasMonitor;
import Vista.VistaInicioMonitores;
import Vista.VistaMensajes;
import Vista.VistaMonitorDialog; // Importamos el diálogo nuevo
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class ControladorMonitor implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInicioMonitores vInicioMonitores;
    private final MonitorDAO monitorDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;

    public ControladorMonitor(VistaInicioMonitores vInicioMonitores, SessionFactory sessionFactory) {
        this.vInicioMonitores = vInicioMonitores;
        this.sessionFactory = sessionFactory;
        this.monitorDAO = new MonitorDAO();
        this.vistaMensajes = new VistaMensajes();
        
        addListeners();
        dibujaRellenaTablaMonitores();
    }
    
    private void addListeners() {
        vInicioMonitores.nuevoMonitor.addActionListener(this);
        vInicioMonitores.nuevoMonitor.setActionCommand("NuevoMonitor");
        
        vInicioMonitores.bajaMonitor.addActionListener(this);
        vInicioMonitores.bajaMonitor.setActionCommand("BajaMonitor");
        
        vInicioMonitores.actualizaciónMonitor.addActionListener(this);
        vInicioMonitores.actualizaciónMonitor.setActionCommand("ActualizarMonitor");
    }

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

    // --- LÓGICA DE NUEVO MONITOR ---
    private void nuevoMonitor() {
        VistaMonitorDialog dialog = new VistaMonitorDialog();
        dialog.setTitle("Nuevo Monitor");
        
        // Calcular siguiente código
        String nuevoCodigo = calcularSiguienteCodigo();
        dialog.textoCodigo.setText(nuevoCodigo);
        dialog.textoCodigo.setEditable(false); // El código no se toca
        
        // Listener del botón Aceptar del diálogo
        dialog.botonAceptar.addActionListener(evt -> {
            insertarMonitorEnBD(dialog);
        });
        
        // Listener del botón Cancelar
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

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
            dialog.dispose(); // Cerrar ventana
            dibujaRellenaTablaMonitores(); // Refrescar tabla
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al insertar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    // --- LÓGICA DE BAJA MONITOR ---
    private void bajaMonitor() {
        // 1. Verificar selección
        int fila = vInicioMonitores.jTableMonitores.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione un monitor para borrar");
            return;
        }
        
        // 2. Obtener código
        String codigo = (String) vInicioMonitores.jTableMonitores.getValueAt(fila, 0);
        
        // 3. Confirmación
        int opt = JOptionPane.showConfirmDialog(null, "¿Seguro que quieres borrar al monitor " + codigo + "?");
        if (opt != JOptionPane.YES_OPTION) return;

        // 4. Borrar en BD
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            Monitor m = monitorDAO.buscarPorCodMonitor(sesion, codigo);
            if (m != null) {
                monitorDAO.borrarMonitor(sesion, m);
                tr.commit();
                dibujaRellenaTablaMonitores(); // Refrescar
            }
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("No se puede borrar (posiblemente tenga actividades asignadas): " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    // --- LÓGICA DE ACTUALIZAR MONITOR ---
    private void actualizarMonitor() {
        int fila = vInicioMonitores.jTableMonitores.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione un monitor para actualizar");
            return;
        }
        
        // 1. Obtener datos de la fila seleccionada
        String codigo = (String) vInicioMonitores.jTableMonitores.getValueAt(fila, 0);
        
        // 2. Cargar datos desde BD (más seguro que desde la tabla)
        Transaction tr = null;
        Monitor m = null;
        try {
            sesion = sessionFactory.openSession();
            m = monitorDAO.buscarPorCodMonitor(sesion, codigo);
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
        
        if (m == null) return;

        // 3. Abrir diálogo y rellenar datos
        VistaMonitorDialog dialog = new VistaMonitorDialog();
        dialog.setTitle("Actualizar Monitor");
        dialog.textoCodigo.setText(m.getCodMonitor());
        dialog.textoCodigo.setEditable(false); // PK no se toca
        
        dialog.textoNombre.setText(m.getNombre());
        dialog.textoDni.setText(m.getDni());
        dialog.textoTelefono.setText(m.getTelefono());
        dialog.textoCorreo.setText(m.getCorreo());
        dialog.textoFecha.setText(m.getFechaEntrada());
        dialog.textoNick.setText(m.getNick());
        
        dialog.botonAceptar.setText("Actualizar");
        
        dialog.botonAceptar.addActionListener(evt -> {
            actualizarMonitorEnBD(dialog);
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private void actualizarMonitorEnBD(VistaMonitorDialog dialog) {
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            // Creamos objeto con los datos nuevos
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

    // --- MÉTODO AUXILIAR PARA CALCULAR CÓDIGO ---
    private String calcularSiguienteCodigo() {
        Transaction tr = null;
        String maxCod = null;
        try {
            sesion = sessionFactory.openSession();
            maxCod = monitorDAO.obtenerUltimoCodigo(sesion);
        } catch(Exception e) {
            // Si la tabla está vacía o falla
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
        
        if (maxCod == null) return "M001";
        
        // Logica simple: Extraer número y sumar 1 (M010 -> 10 -> 11 -> M011)
        try {
            String numPart = maxCod.substring(1);
            int num = Integer.parseInt(numPart) + 1;
            return String.format("M%03d", num); // M + 3 dígitos con ceros a la izquierda
        } catch (Exception e) {
            return "M999"; // Fallback por si el formato no es estándar
        }
    }
}