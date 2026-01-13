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
 * Se encarga de la lógica CRUD (Crear, Leer, Actualizar, Borrar) de los monitores
 * y actualiza la tabla de la interfaz gráfica correspondiente.
 *
 * @author manue
 */

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
    private void nuevoMonitor() {
        VistaMonitorDialog dialog = new VistaMonitorDialog();
        dialog.setTitle("Nuevo Monitor");
        
        String nuevoCodigo = calcularSiguienteCodigo();
        dialog.textoCodigo.setText(nuevoCodigo);
        dialog.textoCodigo.setEditable(false);
        
        dialog.botonAceptar.addActionListener(evt -> {
            insertarMonitorEnBD(dialog);
        });
        
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
            dialog.dispose();
            dibujaRellenaTablaMonitores(); 
            
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al insertar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

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

    private String calcularSiguienteCodigo() {
        Transaction tr = null;
        String maxCod = null;
        try {
            sesion = sessionFactory.openSession();
            maxCod = monitorDAO.obtenerUltimoCodigo(sesion);
        } catch(Exception e) {
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
        
        if (maxCod == null) return "M001";
        
        try {
            String numPart = maxCod.substring(1);
            int num = Integer.parseInt(numPart) + 1;
            return String.format("M%03d", num);
        } catch (Exception e) {
            return "M999"; 
        }
    }
}