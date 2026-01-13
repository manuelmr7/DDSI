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
 * Maneja las operaciones de alta, baja, modificación y listado de socios
 * interactuando con la VistaInicioSocios y el SocioDAO.
 *
 * @author manue
 */

public class ControladorSocio implements ActionListener {
    
    private final SessionFactory sessionFactory;
    private final VistaInicioSocios vInicioSocios;
    private final SocioDAO socioDAO;
    private final VistaMensajes vistaMensajes;
    private Session sesion;


    public ControladorSocio(VistaInicioSocios vInicioSocios, SessionFactory sessionFactory) {
        this.vInicioSocios = vInicioSocios;
        this.sessionFactory = sessionFactory;
        this.socioDAO = new SocioDAO();
        this.vistaMensajes = new VistaMensajes();
        
        addListeners();
        dibujaRellenaTablaSocios();
    }
    
    private void addListeners() {
        vInicioSocios.nuevoSocio.addActionListener(this);
        vInicioSocios.nuevoSocio.setActionCommand("NuevoSocio");
        
        vInicioSocios.bajaSocio.addActionListener(this);
        vInicioSocios.bajaSocio.setActionCommand("BajaSocio");
        
        vInicioSocios.actualizarSocio.addActionListener(this);
        vInicioSocios.actualizarSocio.setActionCommand("ActualizarSocio");
    }
    
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
    private void nuevoSocio()
    {
        VistaSocioDialog dialog=new VistaSocioDialog();
        dialog.setTitle("Nuevo Socio");
        
        String nuevoCodigo=calcularSiguienteCodigo();
        dialog.textoNumeroSocio.setText(nuevoCodigo);
        dialog.textoNumeroSocio.setEditable(false);
        
        cargarCategorias(dialog);
        
        dialog.botonAceptar.addActionListener(evt->insertarSocioEnBD(dialog));
        dialog.botonCancelar.addActionListener(evt->dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true); 
    }
    private void insertarSocioEnBD(VistaSocioDialog dialog)
    {
        Socio s=new Socio();
        s.setNumeroSocio(dialog.textoNumeroSocio.getText());
        s.setNombre(dialog.textoNombre.getText());
        s.setCorreo(dialog.textoCorreo.getText());
        s.setFechaEntrada(dialog.textoFechaEntrada.getText());
        s.setDni(dialog.textoDNI.getText());
        s.setFechaNacimiento(dialog.textoFechaNac.getText());
        s.setTelefono(dialog.textoTelefono.getText());
        String cat=(String) dialog.comboCategoria.getSelectedItem();
        if(cat!=null && !cat.isEmpty())
        {
            s.setCategoria(cat.charAt(0));
        }
        Transaction tr=null;
        try
        {
            sesion=sessionFactory.openSession();
            tr=sesion.beginTransaction();
            socioDAO.insertaSocio(sesion, s);
            tr.commit();
        }
        catch(Exception ex)
        {
            if(tr!=null)
                tr.rollback();
            vistaMensajes.mostrarError("Error al insertar"+ex.getMessage());
        }
        finally
        {
            if(sesion!=null && sesion.isOpen())
                sesion.close();
        }
    }
    private void bajaSocio()
    {
        int fila=vInicioSocios.jTableSocios.getSelectedRow();
        if(fila==-1)
        {
            vistaMensajes.mostrarAdvertencia("Seleccione un socio para borrar");
            return;
        }
        String codigo=(String) vInicioSocios.jTableSocios.getValueAt(fila,0);
        
        int opt=JOptionPane.showConfirmDialog(null, "¿Seguro que quiere borrar al socio "+codigo+" ?");
        if(opt!=JOptionPane.YES_OPTION)
            return;
        Transaction tr=null;
        try
        {
            sesion=sessionFactory.openSession();
            tr=sesion.beginTransaction();
            Socio s=socioDAO.buscarPorNumeroSocio(sesion, codigo);
            if(s!=null)
            {
               socioDAO.borrarSocio(sesion,s);
               tr.commit();
               dibujaRellenaTablaSocios();
            }
        }
        catch(Exception ex)
        {
            if(tr!=null)
                tr.rollback();
            vistaMensajes.mostrarError("No se puede borrar al socio seleccionado");
        }
        finally
        {
            if(sesion!=null && sesion.isOpen())
                sesion.close();
        }
    }
    private void actualizarSocio()
    {
        int fila=vInicioSocios.jTableSocios.getSelectedRow();
        if(fila==-1)
        {
            vistaMensajes.mostrarAdvertencia("Seleecione un socio para actualizar");
            return;
        }
        String codigo=(String) vInicioSocios.jTableSocios.getValueAt(fila,0);
        Transaction tr=null;
        Socio s=null;
        try
        {
            sesion=sessionFactory.openSession();
            s=socioDAO.buscarPorNumeroSocio(sesion, codigo);
        }
        catch(Exception e)
        {
            vistaMensajes.mostrarError("Error al actualizar socio"+e.getMessage());
        }
        finally
        {
            if(sesion!=null && sesion.isOpen())
                sesion.close();
        }
        if(s==null)
        {
            return;
        }
        VistaSocioDialog dialog=new VistaSocioDialog();
        dialog.setTitle("Actualizar Socio");
        cargarCategorias(dialog);
        
        dialog.textoNumeroSocio.setText(s.getNumeroSocio());
        dialog.textoNumeroSocio.setEditable(false);
        dialog.textoCorreo.setText(s.getCorreo());
        dialog.textoDNI.setText(s.getDni());
        dialog.textoFechaEntrada.setText(s.getFechaEntrada());
        dialog.textoFechaNac.setText(s.getFechaNacimiento());
        dialog.textoNombre.setText(s.getNombre());
        dialog.textoTelefono.setText(s.getTelefono());
        
        if(s.getCategoria()!=null)
            dialog.comboCategoria.setSelectedItem(String.valueOf(s.getCategoria()));
         
        dialog.botonAceptar.setText("Actualizar");
        dialog.botonAceptar.addActionListener(evt->actualizarSocioEnBD(dialog));
        dialog.botonCancelar.addActionListener(evt->dialog.dispose());
        
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }
    
    private void actualizarSocioEnBD(VistaSocioDialog dialog)
    {
        Transaction tr=null;
        try
        {
            sesion=sessionFactory.openSession();
            tr=sesion.beginTransaction();
            
            Socio s=new Socio();
            s.setNumeroSocio(dialog.textoNumeroSocio.getText());
            s.setNombre(dialog.textoNombre.getText());
            s.setDni(dialog.textoDNI.getText());
            s.setTelefono(dialog.textoTelefono.getText());
            s.setCorreo(dialog.textoCorreo.getText());
            s.setFechaNacimiento(dialog.textoFechaNac.getText());
            s.setFechaEntrada(dialog.textoFechaEntrada.getText());
            
            String cat=(String) dialog.comboCategoria.getSelectedItem();
            s.setCategoria(cat.charAt(0));
            
            socioDAO.actualizarSocio(sesion, s);
            tr.commit();
            
            vistaMensajes.mostrarInfo("Socio actualizado correctamente");
            dialog.dispose();
            dibujaRellenaTablaSocios();
        }
        catch(Exception ex)
        {
            if(tr!=null)
                tr.rollback();
            vistaMensajes.mostrarError("Error al actualizar: "+ex.getMessage());
        }
        finally
        {
            if(sesion!=null && sesion.isOpen())
                sesion.close();
        }
    }
    
    private void cargarCategorias(VistaSocioDialog dialog)
    {
        String[] categorias={"A","B","C","D","E"};
        dialog.comboCategoria.setModel(new DefaultComboBoxModel<>(categorias));
    }
    
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