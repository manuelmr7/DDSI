package Util;

import Modelo.Socio;
import Vista.VistaInicioSocios;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
/**
 * 
 * @author manue
 */

public class GestionTablasSocio {

    public static DefaultTableModel modeloTablaSocios;

  
    public static void inicializarTablaSocios(VistaInicioSocios vInicio) {
        modeloTablaSocios = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        vInicio.jTableSocios.setModel(modeloTablaSocios);
    }


    public static void dibujarTablaSocios(VistaInicioSocios vInicio) {
        String[] columnas = {"Socio", "Nombre", "DNI", "Fecha Nac.", "Teléfono", "Correo", "Fecha Alta", "Cat."};
        modeloTablaSocios.setColumnIdentifiers(columnas);

        JTable t = vInicio.jTableSocios;

        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setResizingAllowed(false);
        t.getTableHeader().setReorderingAllowed(false);
        t.setAutoCreateRowSorter(true);

        int[] anchuras = {50, 200, 90, 100, 90, 200, 100, 40};
        TableColumnModel modeloColumna = t.getColumnModel();

        for (int i = 0; i < anchuras.length; i++) {
            TableColumn columna = modeloColumna.getColumn(i);
            columna.setMinWidth(anchuras[i]);
            columna.setPreferredWidth(anchuras[i]);
        }
    }


    public static void rellenarTablaSocios(List<Socio> socios) {
        Object[] fila = new Object[8];
        for (Socio s : socios) {
            fila[0] = s.getNumeroSocio();
            fila[1] = s.getNombre();
            fila[2] = s.getDni();
            fila[3] = s.getFechaNacimiento();
            fila[4] = s.getTelefono();
            fila[5] = s.getCorreo();
            fila[6] = s.getFechaEntrada();
            fila[7] = s.getCategoria();
            modeloTablaSocios.addRow(fila);
        }
    }

    public static void vaciarTablaSocios() {
        modeloTablaSocios.setRowCount(0);
    }
}