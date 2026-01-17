package Controlador;

import Modelo.Socio;
import Modelo.SocioDAO;
import Util.GestionTablasSocio;
import Vista.VistaInicioSocios;
import Vista.VistaMensajes;
import Vista.VistaSocioDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Controlador para la gestión de Socios.
 * Maneja las operaciones CRUD (Crear, Leer, Actualizar, Borrar) y las validaciones de negocio
 * como la comprobación de mayoría de edad, fechas lógicas y formatos de datos.
 *
 * @author Manuel Martín Rodrigo
 */
public class ControladorSocio implements ActionListener {

    private final SessionFactory sessionFactory;
    private final VistaInicioSocios vInicioSocios;
    private final SocioDAO socioDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;

    /**
     * Constructor del controlador.
     * Inicializa los componentes, los DAOs y carga la tabla inicial de socios.
     *
     * @param vInicioSocios Vista principal de gestión de socios.
     * @param sessionFactory Fábrica de sesiones de Hibernate para la conexión a BD.
     */
    public ControladorSocio(VistaInicioSocios vInicioSocios, SessionFactory sessionFactory) {
        this.vInicioSocios = vInicioSocios;
        this.sessionFactory = sessionFactory;
        this.socioDAO = new SocioDAO();
        this.vistaMensajes = new VistaMensajes();

        addListeners();
        dibujaRellenaTablaSocios();
    }

    /**
     * Asigna los escuchadores de eventos (listeners) a los botones de la vista principal.
     */
    private void addListeners() {
        vInicioSocios.nuevoSocio.addActionListener(this);
        vInicioSocios.nuevoSocio.setActionCommand("NuevoSocio");
        
        vInicioSocios.bajaSocio.addActionListener(this);
        vInicioSocios.bajaSocio.setActionCommand("BajaSocio");
        
        vInicioSocios.actualizarSocio.addActionListener(this);
        vInicioSocios.actualizarSocio.setActionCommand("ActualizarSocio");
    }

    /**
     * Configura el modelo de la tabla de socios y refresca los datos desde la base de datos.
     * Gestiona la sesión de Hibernate para realizar la consulta de listado.
     */
    private void dibujaRellenaTablaSocios() {
        GestionTablasSocio.inicializarTablaSocios(vInicioSocios);
        GestionTablasSocio.dibujarTablaSocios(vInicioSocios);
        
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            
            List<Socio> listaSocios = socioDAO.listaSocios(sesion);
            
            GestionTablasSocio.vaciarTablaSocios();
            GestionTablasSocio.rellenarTablaSocios(listaSocios);
            
            tr.commit();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al recuperar los socios: " + ex.getMessage());
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
            case "NuevoSocio": nuevoSocio(); break;
            case "BajaSocio": bajaSocio(); break;
            case "ActualizarSocio": actualizarSocio(); break;
        }
    }

    /**
     * Abre el diálogo para registrar un nuevo socio.
     * Calcula el siguiente código disponible y prepara el formulario.
     */
    private void nuevoSocio() {
        VistaSocioDialog dialog = new VistaSocioDialog();
        dialog.setTitle("Nuevo Socio");

        // Cálculo automático del ID (S00X)
        String nuevoCodigo = calcularSiguienteCodigo();
        dialog.textoNumeroSocio.setText(nuevoCodigo);
        dialog.textoNumeroSocio.setEditable(false);

        cargarCategorias(dialog);

        dialog.botonAceptar.addActionListener(evt -> {
            if (validarDatos(dialog)) {
                insertarSocioEnBD(dialog);
            }
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());

        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Valida los datos del formulario de Socio aplicando las reglas de negocio.
     * * Validaciones realizadas:
     * 1. Existencia de fechas en los componentes JCalendar.
     * 2. Fecha de Entrada no futura
     * 3. Mayoría de edad: El socio debe tener 18 años o más
     * 4. Campos obligatorios no vacíos (Nombre, DNI, Correo).
     * 5. Formato correcto de DNI, Teléfono y Correo.
     * * @param dialog Diálogo que contiene los datos a validar.
     * @return true si todos los datos son válidos, false si hay errores.
     */
    private boolean validarDatos(VistaSocioDialog dialog) {
        // 1. Fechas (JCalendar)
        if (dialog.fechaEntradaChooser.getDate() == null || dialog.fechaNacChooser.getDate() == null) {
            vistaMensajes.mostrarAdvertencia("Las fechas de entrada y nacimiento son obligatorias.");
            return false;
        }

        // Lógica Fechas (Conversión a LocalDate para comparar)
        LocalDate fEntrada = dialog.fechaEntradaChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate fNac = dialog.fechaNacChooser.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate hoy = LocalDate.now();

        // Validación: Fecha futura
        if (fEntrada.isAfter(hoy)) {
            vistaMensajes.mostrarError("La fecha de entrada no puede ser futura.");
            return false;
        }

        // Validación: Mayoría de Edad (>= 18 años)
        long edad = ChronoUnit.YEARS.between(fNac, hoy);
        if (edad < 18) {
            vistaMensajes.mostrarError("El socio debe ser mayor de edad (+18). Edad actual: " + edad);
            return false;
        }

        // 2. Campos vacíos
        if (dialog.textoNombre.getText().trim().isEmpty() || 
            dialog.textoDNI.getText().trim().isEmpty() ||
            dialog.textoCorreo.getText().trim().isEmpty()) {
            vistaMensajes.mostrarAdvertencia("Campos obligatorios vacíos (Nombre, DNI, Correo).");
            return false;
        }

        // 3. Validaciones de Formato (Regex)
        if (!dialog.textoDNI.getText().matches("\\d{8}[A-Z]")) {
            vistaMensajes.mostrarError("DNI inválido. Formato: 8 números y 1 letra mayúscula.");
            return false;
        }
        if (!dialog.textoTelefono.getText().matches("\\d{9}")) {
            vistaMensajes.mostrarError("Teléfono inválido. Debe tener 9 dígitos.");
            return false;
        }
        if (!dialog.textoCorreo.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            vistaMensajes.mostrarError("Correo electrónico inválido.");
            return false;
        }

        return true;
    }

    /**
     * Inserta un nuevo socio en la base de datos tras la validación exitosa.
     * Convierte las fechas de JDateChooser a String para almacenarlas.
     * @param dialog Diálogo con los datos del nuevo socio.
     */
    private void insertarSocioEnBD(VistaSocioDialog dialog) {
        Socio s = new Socio();
        s.setNumeroSocio(dialog.textoNumeroSocio.getText());
        s.setNombre(dialog.textoNombre.getText());
        s.setCorreo(dialog.textoCorreo.getText());
        s.setDni(dialog.textoDNI.getText());
        s.setTelefono(dialog.textoTelefono.getText());

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        s.setFechaEntrada(sdf.format(dialog.fechaEntradaChooser.getDate()));
        s.setFechaNacimiento(sdf.format(dialog.fechaNacChooser.getDate()));

        String cat = (String) dialog.comboCategoria.getSelectedItem();
        if (cat != null && !cat.isEmpty()) {
            s.setCategoria(cat.charAt(0));
        }

        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            socioDAO.insertaSocio(sesion, s);
            tr.commit();
            
            vistaMensajes.mostrarInfo("Socio creado correctamente.");
            dialog.dispose();
            dibujaRellenaTablaSocios();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al insertar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Elimina el socio seleccionado de la tabla.
     * Solicita confirmación al usuario antes de proceder.
     */
    private void bajaSocio() {
        int fila = vInicioSocios.jTableSocios.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione un socio para borrar");
            return;
        }
        String codigo = (String) vInicioSocios.jTableSocios.getValueAt(fila, 0);
        int opt = JOptionPane.showConfirmDialog(null, "¿Seguro que quiere borrar al socio " + codigo + "?");
        if (opt != JOptionPane.YES_OPTION) return;

        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            Socio s = socioDAO.buscarPorNumeroSocio(sesion, codigo);
            if (s != null) {
                socioDAO.borrarSocio(sesion, s);
                tr.commit();
                dibujaRellenaTablaSocios();
            }
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("No se puede borrar al socio (puede tener datos asociados).");
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Abre el diálogo de edición cargando los datos del socio seleccionado.
     * Rellena los campos de texto y los selectores de fecha con la información actual.
     */
    private void actualizarSocio() {
        int fila = vInicioSocios.jTableSocios.getSelectedRow();
        if (fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione un socio para actualizar");
            return;
        }
        String codigo = (String) vInicioSocios.jTableSocios.getValueAt(fila, 0);

        Transaction tr = null;
        Socio s = null;
        try {
            sesion = sessionFactory.openSession();
            s = socioDAO.buscarPorNumeroSocio(sesion, codigo);
        } catch (Exception e) {
            vistaMensajes.mostrarError("Error al buscar socio: " + e.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }

        if (s == null) return;

        VistaSocioDialog dialog = new VistaSocioDialog();
        dialog.setTitle("Actualizar Socio");
        cargarCategorias(dialog);

        // Rellenar datos
        dialog.textoNumeroSocio.setText(s.getNumeroSocio());
        dialog.textoNumeroSocio.setEditable(false);
        dialog.textoCorreo.setText(s.getCorreo());
        dialog.textoDNI.setText(s.getDni());
        dialog.textoNombre.setText(s.getNombre());
        dialog.textoTelefono.setText(s.getTelefono());

        // Cargar fechas en JDateChooser
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            if(s.getFechaEntrada() != null) dialog.fechaEntradaChooser.setDate(sdf.parse(s.getFechaEntrada()));
            if(s.getFechaNacimiento() != null) dialog.fechaNacChooser.setDate(sdf.parse(s.getFechaNacimiento()));
        } catch(Exception e) {
            // Si falla el parseo, se dejan vacías o con fecha actual
        }

        if (s.getCategoria() != null) {
            dialog.comboCategoria.setSelectedItem(String.valueOf(s.getCategoria()));
        }

        dialog.botonAceptar.setText("Actualizar");
        dialog.botonAceptar.addActionListener(evt -> {
            if (validarDatos(dialog)) {
                actualizarSocioEnBD(dialog);
            }
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    /**
     * Actualiza la información del socio en la base de datos con los valores modificados.
     * @param dialog Diálogo con los datos actualizados.
     */
    private void actualizarSocioEnBD(VistaSocioDialog dialog) {
        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();

            Socio s = new Socio();
            s.setNumeroSocio(dialog.textoNumeroSocio.getText());
            s.setNombre(dialog.textoNombre.getText());
            s.setDni(dialog.textoDNI.getText());
            s.setTelefono(dialog.textoTelefono.getText());
            s.setCorreo(dialog.textoCorreo.getText());
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            s.setFechaEntrada(sdf.format(dialog.fechaEntradaChooser.getDate()));
            s.setFechaNacimiento(sdf.format(dialog.fechaNacChooser.getDate()));

            String cat = (String) dialog.comboCategoria.getSelectedItem();
            if (cat != null && !cat.isEmpty()) {
                s.setCategoria(cat.charAt(0));
            }

            socioDAO.actualizarSocio(sesion, s);
            tr.commit();

            vistaMensajes.mostrarInfo("Socio actualizado correctamente");
            dialog.dispose();
            dibujaRellenaTablaSocios();
        } catch (Exception ex) {
            if (tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al actualizar: " + ex.getMessage());
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Carga las categorías posibles (A, B, C, D, E) en el desplegable.
     */
    private void cargarCategorias(VistaSocioDialog dialog) {
        String[] categorias = {"A", "B", "C", "D", "E"};
        dialog.comboCategoria.setModel(new DefaultComboBoxModel<>(categorias));
    }

    /**
     * Calcula el siguiente código de socio disponible basándose en el último registrado.
     * Ejemplo: Si el último es S004, devuelve S005.
     * @return String con el nuevo código.
     */
    private String calcularSiguienteCodigo() {
        Transaction tr = null;
        String maxCod = null;
        try {
            sesion = sessionFactory.openSession();
            maxCod = socioDAO.obtenerUltimoCodigo(sesion);
        } catch (Exception e) { 
            // Ignorar errores de conexión puntuales para este cálculo
        } finally {
            if (sesion != null && sesion.isOpen()) sesion.close();
        }
        
        if (maxCod == null) return "S001";
        
        try {
            String numPart = maxCod.substring(1);
            int num = Integer.parseInt(numPart) + 1;
            return String.format("S%03d", num);
        } catch (Exception e) { 
            return "S999"; 
        }
    }
}
