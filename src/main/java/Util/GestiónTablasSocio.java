package Util;

import Modelo.Socio;
import Vista.VistaInicioSocios;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class GestiónTablasSocio {

    public static DefaultTableModel modeloTablaSocios;

    /**
     * Inicializa la tabla en la vista gráfica de listado (VistaInicioSocios).
     * Asegúrate de haber renombrado la tabla en la vista a 'jTableSocios'.
     */
    public static void inicializarTablaSocios(VistaInicioSocios vInicio) {
        modeloTablaSocios = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacemos que las celdas no sean editables
            }
        };
        // Asignamos el modelo a la tabla gráfica
        vInicio.jTableSocios.setModel(modeloTablaSocios);
    }

    /**
     * Define la estructura de columnas y sus anchos visuales.
     */
    public static void dibujarTablaSocios(VistaInicioSocios vInicio) {
        String[] columnas = {"Socio", "Nombre", "DNI", "Fecha Nac.", "Teléfono", "Correo", "Fecha Alta", "Cat."};
        modeloTablaSocios.setColumnIdentifiers(columnas);

        // Referencia a la tabla de la vista gráfica
        JTable t = vInicio.jTableSocios;

        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setResizingAllowed(false);
        t.getTableHeader().setReorderingAllowed(false);
        t.setAutoCreateRowSorter(true);

        // Definimos anchos estimados para que quede bien visualmente
        int[] anchuras = {50, 200, 90, 100, 90, 200, 100, 40};
        TableColumnModel modeloColumna = t.getColumnModel();

        for (int i = 0; i < anchuras.length; i++) {
            TableColumn columna = modeloColumna.getColumn(i);
            columna.setMinWidth(anchuras[i]);
            columna.setPreferredWidth(anchuras[i]);
        }
    }

    /**
     * Rellena la tabla con una lista de objetos Socio.
     */
    public static void rellenarTablaSocios(List<Socio> socios) {
        Object[] fila = new Object[8]; // 8 columnas
        for (Socio s : socios) {
            fila[0] = s.getNumeroSocio();
            fila[1] = s.getNombre();
            fila[2] = s.getDni();
            fila[3] = s.getFechaNacimiento();
            fila[4] = s.getTelefono();
            fila[5] = s.getCorreo();
            fila[6] = s.getFechaEntrada();
            fila[7] = s.getCategoria(); // Asumimos que es un Character
            modeloTablaSocios.addRow(fila);
        }
    }

    /**
     * Vacía el contenido de la tabla.
     */
    public static void vaciarTablaSocios() {
        modeloTablaSocios.setRowCount(0);
    }
}