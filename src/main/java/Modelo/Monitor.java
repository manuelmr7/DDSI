package Modelo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Entidad que representa a un Monitor del gimnasio. Mapeada a la tabla
 * "MONITOR" de la base de datos. Incluye consultas predefinidas para buscar por
 * distintos campos.
 *
 * * @author manue
 */
@Entity
@Table(name = "MONITOR")
@NamedQueries({
    @NamedQuery(name = "Monitor.findAll", query = "SELECT m FROM Monitor m"),
    @NamedQuery(name = "Monitor.findByCodMonitor", query = "SELECT m FROM Monitor m WHERE m.codMonitor = :codMonitor"),
    @NamedQuery(name = "Monitor.findByNombre", query = "SELECT m FROM Monitor m WHERE m.nombre = :nombre"),
    @NamedQuery(name = "Monitor.findByDni", query = "SELECT m FROM Monitor m WHERE m.dni = :dni"),
    @NamedQuery(name = "Monitor.findByTelefono", query = "SELECT m FROM Monitor m WHERE m.telefono = :telefono"),
    @NamedQuery(name = "Monitor.findByCorreo", query = "SELECT m FROM Monitor m WHERE m.correo = :correo"),
    @NamedQuery(name = "Monitor.findByFechaEntrada", query = "SELECT m FROM Monitor m WHERE m.fechaEntrada = :fechaEntrada"),
    @NamedQuery(name = "Monitor.findByNick", query = "SELECT m FROM Monitor m WHERE m.nick = :nick")})
public class Monitor implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Código único del monitor (Clave Primaria).
     */
    @Id
    @Basic(optional = false)
    @Column(name = "codMonitor")
    private String codMonitor;

    /**
     * Nombre completo del monitor.
     */
    @Basic(optional = false)
    @Column(name = "nombre")
    private String nombre;

    /**
     * Documento Nacional de Identidad.
     */
    @Basic(optional = false)
    @Column(name = "dni")
    private String dni;

    /**
     * Teléfono de contacto.
     */
    @Column(name = "telefono")
    private String telefono;

    /**
     * Correo electrónico.
     */
    @Column(name = "correo")
    private String correo;

    /**
     * Fecha de incorporación al gimnasio.
     */
    @Basic(optional = false)
    @Column(name = "fechaEntrada")
    private String fechaEntrada;

    /**
     * Apodo o nick del monitor.
     */
    @Column(name = "nick")
    private String nick;

    /**
     * Lista de actividades de las que este monitor es responsable.
     */
    @OneToMany(mappedBy = "monitorResponsable")
    private Set<Actividad> actividadesResponsable = new HashSet<Actividad>();

    /**
     * Constructor vacío requerido por JPA.
     */
    public Monitor() {
    }

    /**
     * Constructor con la clave primaria.
     *
     * @param codMonitor Código del monitor.
     */
    public Monitor(String codMonitor) {
        this.codMonitor = codMonitor;
    }

    /**
     * Constructor con campos obligatorios.
     *
     * @param codMonitor Código.
     * @param nombre Nombre.
     * @param dni DNI.
     * @param fechaEntrada Fecha de entrada.
     */
    public Monitor(String codMonitor, String nombre, String dni, String fechaEntrada) {
        this.codMonitor = codMonitor;
        this.nombre = nombre;
        this.dni = dni;
        this.fechaEntrada = fechaEntrada;
    }

    /**
     * Constructor completo.
     *
     * @param codMonitor Código.
     * @param nombre Nombre.
     * @param dni DNI.
     * @param telefono Teléfono.
     * @param correo Correo.
     * @param fechaEntrada Fecha de entrada.
     * @param nick Nick.
     */
    public Monitor(String codMonitor, String nombre, String dni, String telefono, String correo, String fechaEntrada, String nick) {
        this.codMonitor = codMonitor;
        this.nombre = nombre;
        this.dni = dni;
        this.telefono = telefono;
        this.correo = correo;
        this.fechaEntrada = fechaEntrada;
        this.nick = nick;
    }

    // --- Getters y Setters Documentados ---
    /**
     * @return El código del monitor.
     */
    public String getCodMonitor() {
        return codMonitor;
    }

    /**
     * @param codMonitor Nuevo código.
     */
    public void setCodMonitor(String codMonitor) {
        this.codMonitor = codMonitor;
    }

    /**
     * @return El nombre del monitor.
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
     * @return El DNI.
     */
    public String getDni() {
        return dni;
    }

    /**
     * @param dni Nuevo DNI.
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * @return El teléfono.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono Nuevo teléfono.
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return El correo electrónico.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo Nuevo correo.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * @return La fecha de entrada.
     */
    public String getFechaEntrada() {
        return fechaEntrada;
    }

    /**
     * @param fechaEntrada Nueva fecha.
     */
    public void setFechaEntrada(String fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    /**
     * @return El nick o apodo.
     */
    public String getNick() {
        return nick;
    }

    /**
     * @param nick Nuevo nick.
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * @return Conjunto de actividades que imparte.
     */
    public Set<Actividad> getActividadSet() {
        return actividadesResponsable;
    }

    /**
     * @param actividadesResponsable Nuevo conjunto de actividades.
     */
    public void setActividadSet(Set<Actividad> actividadesResponsable) {
        this.actividadesResponsable = actividadesResponsable;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (codMonitor != null ? codMonitor.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Monitor)) {
            return false;
        }
        Monitor other = (Monitor) object;
        if ((this.codMonitor == null && other.codMonitor != null) || (this.codMonitor != null && !this.codMonitor.equals(other.codMonitor))) {
            return false;
        }
        return true;
    }

    /**
     * Devuelve el nombre del monitor para mostrarlo correctamente en los
     * desplegables (ComboBox).
     *
     * @return Nombre del monitor.
     */
    @Override
    public String toString() {
        return nombre;
    }
}
