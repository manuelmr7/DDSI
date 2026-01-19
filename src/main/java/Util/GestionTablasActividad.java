package Util;

import Modelo.Actividad;
import Vista.VistaInicioActividades;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 * Clase de utilidad para la gestión visual de la tabla de Actividades.
 * Se encarga de inicializar el modelo, definir el diseño de las columnas y
 * rellenar o vaciar los datos en la interfaz gráfica.
 *
 * @author Manuel Martín Rodrigo
 */
public class GestionTablasActividad {

    /**
     * Modelo de datos para la JTable de Actividades.
     */
    public static DefaultTableModel modeloTablaActividades;

    /**
     * Inicializa el modelo de la tabla y lo asigna a la vista.
     * Configura la tabla para que las celdas no sean editables por el usuario.
     *
     * @param vInicio Vista principal de actividades que contiene la JTable.
     */
    public static void inicializarTablaActividades(VistaInicioActividades vInicio) {
        modeloTablaActividades = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vInicio.jTableActividades.setModel(modeloTablaActividades);
    }

    /**
     * Define la estructura visual de la tabla: nombres de columnas, anchos y propiedades.
     * Establece cabeceras como "Código", "Nombre", "Día", etc., y ajusta el tamaño de cada columna.
     *
     * @param vInicio Vista principal de actividades.
     */
    public static void dibujarTablaActividades(VistaInicioActividades vInicio) {
        String[] columnas = {"Código", "Nombre", "Día", "Hora", "Precio", "Monitor Resp.", "Descripción"};
        modeloTablaActividades.setColumnIdentifiers(columnas);

        JTable t = vInicio.jTableActividades;
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setResizingAllowed(false);
        t.getTableHeader().setReorderingAllowed(false);
        t.setAutoCreateRowSorter(true);

        // Definición de anchos específicos para mejorar la visualización
        int[] anchuras = {60, 150, 80, 50, 50, 200,400};
        TableColumnModel modeloColumna = t.getColumnModel();

        for (int i = 0; i < anchuras.length; i++) {
            TableColumn columna = modeloColumna.getColumn(i);
            columna.setMinWidth(anchuras[i]);
            columna.setPreferredWidth(anchuras[i]);
        }
    }

    /**
     * Rellena la tabla con la lista de actividades proporcionada.
     * Convierte cada objeto Actividad en una fila de la tabla.
     *
     * @param actividades Lista de actividades a mostrar en la tabla.
     */
    public static void rellenarTablaActividades(List<Actividad> actividades) {
        Object[] fila = new Object[7];
        for (Actividad a : actividades) {
            fila[0] = a.getIdActividad();
            fila[1] = a.getNombre();
            fila[2] = a.getDia();
            fila[3] = a.getHora();
            fila[4] = a.getPrecioBaseMes();
            
            // Verificamos si hay monitor asignado para evitar NullPointerException
            if (a.getMonitorResponsable() != null) {
                fila[5] = a.getMonitorResponsable().getNombre();
            } else {
                fila[5] = "Sin Asignar";
            }
            fila[6]=a.getDescripcion();
            modeloTablaActividades.addRow(fila);
        }
    }

    /**
     * Elimina todas las filas de la tabla para dejarla vacía.
     * Útil antes de refrescar los datos tras una búsqueda o actualización.
     */
    public static void vaciarTablaActividades() {
        modeloTablaActividades.setRowCount(0);
    }
}