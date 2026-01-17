package Controlador;

import Modelo.Socio;
import Modelo.SocioDAO;
import Util.GestionTablasSocio;
import Vista.VistaInicioSocios;
import Vista.VistaMensajes;
import Vista.VistaSocioDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/**
 * Controlador para la gestión de Socios.
 * Maneja las operaciones de alta, baja, modificación y listado de socios,
 * incluyendo validaciones de formato para DNI y Teléfono.
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
     * Inicializa la vista, el DAO y configura la tabla de socios.
     *
     * @param vInicioSocios Vista principal de gestión de socios.
     * @param sessionFactory Fábrica de sesiones de Hibernate.
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
     * Asigna los listeners a los botones de la vista.
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
     * Configura y rellena la tabla de socios con los datos de la base de datos.
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
            if (sesion != null && sesion.isOpen()) {
                sesion.close();
            }
        }
    }

    /**
     * Maneja los eventos de los botones.
     * @param e Evento disparado.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "NuevoSocio":
                nuevoSocio();
                break;
            case "BajaSocio":
                bajaSocio();
                break;
            case "ActualizarSocio":
                actualizarSocio();
                break;
        }
    }

    /**
     * Abre el diálogo para registrar un nuevo socio.
     * Calcula automáticamente el siguiente código disponible.
     */
    private void nuevoSocio() {
        VistaSocioDialog dialog = new VistaSocioDialog();
        dialog.setTitle("Nuevo Socio");
        
        String nuevoCodigo = calcularSiguienteCodigo();
        dialog.textoNumeroSocio.setText(nuevoCodigo);
        dialog.textoNumeroSocio.setEditable(false);
        
        cargarCategorias(dialog);
        
        dialog.botonAceptar.addActionListener(evt -> {
            // Validamos antes de insertar
            if (validarDatos(dialog)) {
                insertarSocioEnBD(dialog);
            }
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true); 
    }

    /**
     * Valida los datos introducidos en el formulario de Socio.
     * Comprueba formato de DNI y longitud de teléfono.
     *
     * @param dialog Diálogo con los campos de texto.
     * @return true si los datos son correctos, false si hay errores.
     */
    private boolean validarDatos(VistaSocioDialog dialog) {
        // 1. Campos obligatorios
        if (dialog.textoNombre.getText().trim().isEmpty() || 
            dialog.textoDNI.getText().trim().isEmpty() ||
            dialog.textoCorreo.getText().trim().isEmpty()) {
            vistaMensajes.mostrarAdvertencia("El Nombre, DNI y Correo son obligatorios.");
            return false;
        }

        // 2. Validación de DNI (8 dígitos y 1 letra mayúscula)
        if (!dialog.textoDNI.getText().matches("\\d{8}[A-Z]")) {
            vistaMensajes.mostrarError("El DNI debe tener 8 números y una letra mayúscula (Ej: 12345678Z).");
            return false;
        }

        // 3. Validación de Teléfono (9 dígitos numéricos)
        if (!dialog.textoTelefono.getText().matches("\\d{9}")) {
            vistaMensajes.mostrarError("El teléfono debe constar de 9 dígitos.");
            return false;
        }

        return true;
    }

    /**
     * Inserta el socio en la base de datos tras la validación.
     * @param dialog Diálogo con los datos.
     */
    private void insertarSocioEnBD(VistaSocioDialog dialog) {
        Socio s = new Socio();
        s.setNumeroSocio(dialog.textoNumeroSocio.getText());
        s.setNombre(dialog.textoNombre.getText());
        s.setCorreo(dialog.textoCorreo.getText());
        s.setFechaEntrada(dialog.textoFechaEntrada.getText());
        s.setDni(dialog.textoDNI.getText());
        s.setFechaNacimiento(dialog.textoFechaNac.getText());
        s.setTelefono(dialog.textoTelefono.getText());
        
        String cat = (String) dialog.comboCategoria.getSelectedItem();
        if(cat != null && !cat.isEmpty()) {
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
        } catch(Exception ex) {
            if(tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al insertar: " + ex.getMessage());
        } finally {
            if(sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Elimina el socio seleccionado de la tabla.
     */
    private void bajaSocio() {
        int fila = vInicioSocios.jTableSocios.getSelectedRow();
        if(fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione un socio para borrar");
            return;
        }
        String codigo = (String) vInicioSocios.jTableSocios.getValueAt(fila, 0);
        
        int opt = JOptionPane.showConfirmDialog(null, "¿Seguro que quiere borrar al socio " + codigo + "?");
        if(opt != JOptionPane.YES_OPTION) return;

        Transaction tr = null;
        try {
            sesion = sessionFactory.openSession();
            tr = sesion.beginTransaction();
            Socio s = socioDAO.buscarPorNumeroSocio(sesion, codigo);
            if(s != null) {
               socioDAO.borrarSocio(sesion, s);
               tr.commit();
               dibujaRellenaTablaSocios();
            }
        } catch(Exception ex) {
            if(tr != null) tr.rollback();
            vistaMensajes.mostrarError("No se puede borrar al socio (posiblemente tenga actividades o inscripciones).");
        } finally {
            if(sesion != null && sesion.isOpen()) sesion.close();
        }
    }

    /**
     * Abre el diálogo de edición con los datos del socio cargados.
     */
    private void actualizarSocio() {
        int fila = vInicioSocios.jTableSocios.getSelectedRow();
        if(fila == -1) {
            vistaMensajes.mostrarAdvertencia("Seleccione un socio para actualizar");
            return;
        }
        String codigo = (String) vInicioSocios.jTableSocios.getValueAt(fila, 0);
        
        Transaction tr = null;
        Socio s = null;
        try {
            sesion = sessionFactory.openSession();
            s = socioDAO.buscarPorNumeroSocio(sesion, codigo);
        } catch(Exception e) {
            vistaMensajes.mostrarError("Error al buscar socio: " + e.getMessage());
        } finally {
            if(sesion != null && sesion.isOpen()) sesion.close();
        }

        if(s == null) return;

        VistaSocioDialog dialog = new VistaSocioDialog();
        dialog.setTitle("Actualizar Socio");
        cargarCategorias(dialog);
        
        // Rellenar campos
        dialog.textoNumeroSocio.setText(s.getNumeroSocio());
        dialog.textoNumeroSocio.setEditable(false);
        dialog.textoCorreo.setText(s.getCorreo());
        dialog.textoDNI.setText(s.getDni());
        dialog.textoFechaEntrada.setText(s.getFechaEntrada());
        dialog.textoFechaNac.setText(s.getFechaNacimiento());
        dialog.textoNombre.setText(s.getNombre());
        dialog.textoTelefono.setText(s.getTelefono());
        
        if(s.getCategoria() != null)
            dialog.comboCategoria.setSelectedItem(String.valueOf(s.getCategoria()));
         
        dialog.botonAceptar.setText("Actualizar");
        
        dialog.botonAceptar.addActionListener(evt -> {
            // Validamos antes de actualizar
            if (validarDatos(dialog)) {
                actualizarSocioEnBD(dialog);
            }
        });
        dialog.botonCancelar.addActionListener(evt -> dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    /**
     * Actualiza los datos del socio en la base de datos.
     * @param dialog Diálogo con los datos modificados.
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
            s.setFechaNacimiento(dialog.textoFechaNac.getText());
            s.setFechaEntrada(dialog.textoFechaEntrada.getText());
            
            String cat = (String) dialog.comboCategoria.getSelectedItem();
            if (cat != null && !cat.isEmpty()) {
                s.setCategoria(cat.charAt(0));
            }
            
            socioDAO.actualizarSocio(sesion, s);
            tr.commit();
            
            vistaMensajes.mostrarInfo("Socio actualizado correctamente");
            dialog.dispose();
            dibujaRellenaTablaSocios();
        } catch(Exception ex) {
            if(tr != null) tr.rollback();
            vistaMensajes.mostrarError("Error al actualizar: " + ex.getMessage());
        } finally {
            if(sesion != null && sesion.isOpen()) sesion.close();
        }
    }
    
    /**
     * Carga las categorías en el ComboBox del diálogo.
     */
    private void cargarCategorias(VistaSocioDialog dialog) {
        String[] categorias = {"A", "B", "C", "D", "E"};
        dialog.comboCategoria.setModel(new DefaultComboBoxModel<>(categorias));
    }
    
    /**
     * Calcula el siguiente código de socio disponible.
     * @return El nuevo código (ej: S005).
     */
    private String calcularSiguienteCodigo() {
        Transaction tr = null;
        String maxCod = null;
        try {
            sesion = sessionFactory.openSession();
            maxCod = socioDAO.obtenerUltimoCodigo(sesion);
        } catch(Exception e) { 
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