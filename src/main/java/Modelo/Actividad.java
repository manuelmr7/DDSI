package Modelo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa una Actividad del gimnasio. Mapeada a la tabla
 * "ACTIVIDAD" de la base de datos. Incluye consultas (NamedQueries) para
 * búsquedas por atributos.
 *
 * * @author manue
 */
@Entity
@Table(name = "ACTIVIDAD")
@NamedQueries({
    @NamedQuery(name = "Actividad.findAll", query = "SELECT a FROM Actividad a"),
    @NamedQuery(name = "Actividad.findByIdActividad", query = "SELECT a FROM Actividad a WHERE a.idActividad = :idActividad"),
    @NamedQuery(name = "Actividad.findByNombre", query = "SELECT a FROM Actividad a WHERE a.nombre = :nombre"),
    @NamedQuery(name = "Actividad.findByDia", query = "SELECT a FROM Actividad a WHERE a.dia = :dia"),
    @NamedQuery(name = "Actividad.findByHora", query = "SELECT a FROM Actividad a WHERE a.hora = :hora"),
    @NamedQuery(name = "Actividad.findByDescripcion", query = "SELECT a FROM Actividad a WHERE a.descripcion = :descripcion"),
    @NamedQuery(name = "Actividad.findByPrecioBaseMes", query = "SELECT a FROM Actividad a WHERE a.precioBaseMes = :precioBaseMes")})
public class Actividad implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Identificador único de la actividad (Clave Primaria).
     */
    @Id
    @Basic(optional = false)
    @Column(name = "idActividad")
    private String idActividad;

    /**
     * Nombre de la actividad.
     */
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    /**
     * Día de la semana en que se imparte.
     */
    @Basic(optional = false)
    @Column(name = "dia")
    private String dia;

    /**
     * Hora de inicio de la actividad.
     */
    @Basic(optional = false)
    @Column(name = "hora")
    private int hora;

    /**
     * Descripción detallada.
     */
    @Column(name = "descripcion")
    private String descripcion;

    /**
     * Precio base mensual de la actividad.
     */
    @Basic(optional = false)
    @Column(name = "precioBaseMes")
    private int precioBaseMes;

    /**
     * Conjunto de socios inscritos en la actividad (Relación N:M).
     */
    @JoinTable(name = "REALIZA", joinColumns = {
        @JoinColumn(name = "idActividad", referencedColumnName = "idActividad")}, inverseJoinColumns = {
        @JoinColumn(name = "numeroSocio", referencedColumnName = "numeroSocio")})
    @ManyToMany
    private Set<Socio> socios = new HashSet<Socio>();

    /**
     * Monitor responsable de impartir la actividad.
     */
    @JoinColumn(name = "monitorResponsable", referencedColumnName = "codMonitor")
    @ManyToOne
    private Monitor monitorResponsable;

    /**
     * Constructor vacío requerido por JPA.
     */
    public Actividad() {
    }

    /**
     * Constructor con el identificador.
     *
     * @param idActividad ID de la actividad.
     */
    public Actividad(String idActividad) {
        this.idActividad = idActividad;
    }

    /**
     * Constructor con campos básicos.
     *
     * @param idActividad ID de la actividad.
     * @param nombre Nombre.
     * @param dia Día de la semana.
     * @param hora Hora de inicio.
     * @param precioBaseMes Precio mensual.
     */
    public Actividad(String idActividad, String nombre, String dia, int hora, int precioBaseMes) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.dia = dia;
        this.hora = hora;
        this.precioBaseMes = precioBaseMes;
    }

    /**
     * Constructor completo.
     *
     * @param idActividad ID de la actividad.
     * @param nombre Nombre.
     * @param dia Día.
     * @param hora Hora.
     * @param descripcion Descripción.
     * @param precioBase Precio.
     * @param monitorResponsable Objeto Monitor responsable (Corregido de String
     * a Monitor).
     */
    public Actividad(String idActividad, String nombre, String dia, int hora, String descripcion, int precioBase, Monitor monitorResponsable) {
        this.idActividad = idActividad;
        this.nombre = nombre;
        this.dia = dia;
        this.hora = hora;
        this.descripcion = descripcion;
        this.precioBaseMes = precioBase;
        this.monitorResponsable = monitorResponsable;
    }

    // --- Getters y Setters Documentados ---
    /**
     * @return El ID de la actividad.
     */
    public String getIdActividad() {
        return idActividad;
    }

    /**
     * @param idActividad Nuevo ID.
     */
    public void setIdActividad(String idActividad) {
        this.idActividad = idActividad;
    }

    /**
     * @return El nombre de la actividad.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return El día de la semana.
     */
    public String getDia() {
        return dia;
    }

    /**
     * @param dia Nuevo día.
     */
    public void setDia(String dia) {
        this.dia = dia;
    }

    /**
     * @return La hora de la actividad.
     */
    public int getHora() {
        return hora;
    }

    /**
     * @param hora Nueva hora.
     */
    public void setHora(int hora) {
        this.hora = hora;
    }

    /**
     * @return La descripción.
     */
    public String getDescripcion() {
        return descripcion;
    }

    /**
     * @param descripcion Nueva descripción.
     */
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * @return El precio base mensual.
     */
    public int getPrecioBaseMes() {
        return precioBaseMes;
    }

    /**
     * @param precioBaseMes Nuevo precio.
     */
    public void setPrecioBaseMes(int precioBaseMes) {
        this.precioBaseMes = precioBaseMes;
    }

    /**
     * @return El conjunto de socios inscritos.
     */
    public Set<Socio> getSocioSet() {
        return socios;
    }

    /**
     * @param socioSet Nuevo conjunto de socios.
     */
    public void setSocioSet(Set<Socio> socioSet) {
        this.socios = socioSet;
    }

    /**
     * @return El monitor responsable.
     */
    public Monitor getMonitorResponsable() {
        return monitorResponsable;
    }

    /**
     * @param monitorResponsable Nuevo monitor responsable.
     */
    public void setMonitorResponsable(Monitor monitorResponsable) {
        this.monitorResponsable = monitorResponsable;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (idActividad != null ? idActividad.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Actividad)) {
            return false;
        }
        Actividad other = (Actividad) object;
        if ((this.idActividad == null && other.idActividad != null) || (this.idActividad != null && !this.idActividad.equals(other.idActividad))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Actividad[ idActividad=" + idActividad + " ]";
    }

    /**
     * Añade un socio a la actividad y mantiene la coherencia bidireccional.
     *
     * @param socio Socio a inscribir.
     */
    public void agregarSocio(Socio socio) {
        this.socios.add(socio);
        socio.getActividadSet().add(this);
    }

    /**
     * Elimina un socio de la actividad y mantiene la coherencia bidireccional.
     *
     * @param socio Socio a dar de baja.
     */
    public void eliminarSocio(Socio socio) {
        this.socios.remove(socio);
        socio.getActividadSet().remove(this);
    }
}
