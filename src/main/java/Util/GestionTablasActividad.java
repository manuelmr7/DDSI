package Util;

import Modelo.Actividad;
import Vista.VistaInicioActividades;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class GestionTablasActividad {

    public static DefaultTableModel modeloTablaActividades;

    public static void inicializarTablaActividades(VistaInicioActividades vInicio) {
        modeloTablaActividades = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vInicio.jTableActividades.setModel(modeloTablaActividades);
    }

    public static void dibujarTablaActividades(VistaInicioActividades vInicio) {
        String[] columnas = {"Código", "Nombre", "Día", "Hora", "Precio", "Monitor Resp."};
        modeloTablaActividades.setColumnIdentifiers(columnas);

        JTable t = vInicio.jTableActividades;
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setResizingAllowed(false);
        t.getTableHeader().setReorderingAllowed(false);
        t.setAutoCreateRowSorter(true);

        // Ajusta los anchos según el contenido
        int[] anchuras = {60, 150, 80, 50, 50, 200};
        TableColumnModel modeloColumna = t.getColumnModel();

        for (int i = 0; i < anchuras.length; i++) {
            TableColumn columna = modeloColumna.getColumn(i);
            columna.setMinWidth(anchuras[i]);
            columna.setPreferredWidth(anchuras[i]);
        }
    }

    public static void rellenarTablaActividades(List<Actividad> actividades) {
        Object[] fila = new Object[6];
        for (Actividad a : actividades) {
            fila[0] = a.getIdActividad();
            fila[1] = a.getNombre();
            fila[2] = a.getDia();
            fila[3] = a.getHora();
            fila[4] = a.getPrecioBaseMes();
            // Obtenemos el nombre del monitor responsable (controlando nulos)
            if (a.getMonitorResponsable() != null) {
                fila[5] = a.getMonitorResponsable().getNombre();
            } else {
                fila[5] = "Sin Asignar";
            }
            modeloTablaActividades.addRow(fila);
        }
    }

    public static void vaciarTablaActividades() {
        modeloTablaActividades.setRowCount(0);
    }
}