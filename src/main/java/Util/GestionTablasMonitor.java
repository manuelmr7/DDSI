package Util;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.List;
import Modelo.Monitor;
// IMPORTANTE: Importamos la vista gráfica correcta
import Vista.VistaInicioMonitores; 

public class GestionTablasMonitor {

    public static DefaultTableModel modeloTablaMonitores;

    /**
     * Inicializa la tabla en la vista gráfica de listado (VistaInicioMonitores)
     */
    public static void inicializarTablaMonitores(VistaInicioMonitores vInicio) {
        modeloTablaMonitores = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        // Asignamos el modelo a la tabla que está en la ventana gráfica
        vInicio.jTableMonitores.setModel(modeloTablaMonitores);
    }
    
    

    public static void dibujarTablaMonitores(VistaInicioMonitores vInicio) {
        String[] columnas = {"Código", "Nombre", "DNI", "Teléfono", "Correo", "Fecha Incorporación", "Nick"};
        modeloTablaMonitores.setColumnIdentifiers(columnas);

        // Referencia a la tabla de la vista gráfica
        JTable t = vInicio.jTableMonitores;
        
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setResizingAllowed(false);
        t.getTableHeader().setReorderingAllowed(false);
        t.setAutoCreateRowSorter(true); 

        int[] anchuras = {60, 300, 100, 100, 250, 150, 100};
        TableColumnModel modeloColumna = t.getColumnModel();

        for (int i = 0; i < anchuras.length; i++) {
            TableColumn columna = modeloColumna.getColumn(i);
            columna.setMinWidth(anchuras[i]);
            columna.setPreferredWidth(anchuras[i]);
        }
    }

    public static void rellenarTablaMonitores(List<Monitor> monitores) {
        Object[] fila = new Object[7];
        for (Monitor monitor : monitores) {
            fila[0] = monitor.getCodMonitor();
            fila[1] = monitor.getNombre();
            fila[2] = monitor.getDni();
            fila[3] = monitor.getTelefono();
            fila[4] = monitor.getCorreo();
            fila[5] = monitor.getFechaEntrada();
            fila[6] = monitor.getNick();
            modeloTablaMonitores.addRow(fila);
        }
    }

    public static void vaciarTablaMonitores() {
        modeloTablaMonitores.setRowCount(0);
    }
}