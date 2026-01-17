package Util;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.util.List;
import Modelo.Monitor;
import Vista.VistaInicioMonitores; 

/**
 * Clase de utilidad para la gestión y diseño de la tabla de Monitores.
 * Se encarga de inicializar el modelo, definir las columnas y rellenar los datos
 * en la interfaz gráfica.
 *
 * @author Manuel Martín Rodrigo
 */
public class GestionTablasMonitor {

    /**
     * Modelo de datos subyacente para la JTable de Monitores.
     */
    public static DefaultTableModel modeloTablaMonitores;

    /**
     * Inicializa el modelo de la tabla y lo asigna a la vista.
     * Configura la tabla para que las celdas no sean editables por el usuario.
     *
     * @param vInicio Vista principal de monitores que contiene la JTable.
     */
    public static void inicializarTablaMonitores(VistaInicioMonitores vInicio) {
        modeloTablaMonitores = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vInicio.jTableMonitores.setModel(modeloTablaMonitores);
    }
    
    /**
     * Define la estructura visual de la tabla: cabeceras y ancho de columnas.
     * Establece los identificadores de columna ("Código", "Nombre", "DNI", etc.)
     * y configura propiedades como el reordenamiento y el auto-ajuste.
     *
     * @param vInicio Vista principal de monitores.
     */
    public static void dibujarTablaMonitores(VistaInicioMonitores vInicio) {
        String[] columnas = {"Código", "Nombre", "DNI", "Teléfono", "Correo", "Fecha Incorporación", "Nick"};
        modeloTablaMonitores.setColumnIdentifiers(columnas);

        JTable t = vInicio.jTableMonitores;
        
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setResizingAllowed(false);
        t.getTableHeader().setReorderingAllowed(false);
        t.setAutoCreateRowSorter(true); 

        // Configuración de anchos específicos para cada columna
        int[] anchuras = {60, 300, 100, 100, 250, 150, 100};
        TableColumnModel modeloColumna = t.getColumnModel();

        for (int i = 0; i < anchuras.length; i++) {
            TableColumn columna = modeloColumna.getColumn(i);
            columna.setMinWidth(anchuras[i]);
            columna.setPreferredWidth(anchuras[i]);
        }
    }

    /**
     * Rellena la tabla con la lista de monitores proporcionada.
     * Convierte cada objeto Monitor en una fila de la tabla.
     *
     * @param monitores Lista de monitores recuperados de la base de datos.
     */
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

    /**
     * Elimina todas las filas de la tabla.
     * Se debe llamar antes de rellenar la tabla para evitar duplicados.
     */
    public static void vaciarTablaMonitores() {
        modeloTablaMonitores.setRowCount(0);
    }
}