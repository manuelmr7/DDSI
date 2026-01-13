package Modelo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase entidad que representa un Socio del gimnasio. Mapeada a la tabla
 * "SOCIO" de la base de datos usando anotaciones JPA. Incluye consultas
 * predefinidas (NamedQueries) para búsquedas por diferentes campos.
 *
 * * @author manue
 */
@Entity
@Table(name = "SOCIO")
@NamedQueries({
    @NamedQuery(name = "Socio.findAll", query = "SELECT s FROM Socio s"),
    @NamedQuery(name = "Socio.findByNumeroSocio", query = "SELECT s FROM Socio s WHERE s.numeroSocio = :numeroSocio"),
    @NamedQuery(name = "Socio.findByNombre", query = "SELECT s FROM Socio s WHERE s.nombre = :nombre"),
    @NamedQuery(name = "Socio.findByDni", query = "SELECT s FROM Socio s WHERE s.dni = :dni"),
    @NamedQuery(name = "Socio.findByFechaNacimiento", query = "SELECT s FROM Socio s WHERE s.fechaNacimiento = :fechaNacimiento"),
    @NamedQuery(name = "Socio.findByTelefono", query = "SELECT s FROM Socio s WHERE s.telefono = :telefono"),
    @NamedQuery(name = "Socio.findByCorreo", query = "SELECT s FROM Socio s WHERE s.correo = :correo"),
    @NamedQuery(name = "Socio.findByFechaEntrada", query = "SELECT s FROM Socio s WHERE s.fechaEntrada = :fechaEntrada"),
    @NamedQuery(name = "Socio.findByCategoria", query = "SELECT s FROM Socio s WHERE s.categoria = :categoria")})
public class Socio implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Número de socio único (Clave Primaria).
     */
    @Id
    @Basic(optional = false)
    @Column(name = "numeroSocio")
    private String numeroSocio;

    /**
     * Nombre completo del socio.
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
     * Fecha de nacimiento del socio.
     */
    @Column(name = "fechaNacimiento")
    private String fechaNacimiento;

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
     * Fecha en la que el socio se dio de alta.
     */
    @Basic(optional = false)
    @Column(name = "fechaEntrada")
    private String fechaEntrada;

    /**
     * Categoría del socio (A, B, C, D, E).
     */
    @Basic(optional = false)
    @Column(name = "categoria")
    private Character categoria;

    /**
     * Conjunto de actividades en las que está inscrito el socio.
     */
    @ManyToMany(mappedBy = "socios")
    private Set<Actividad> actividades = new HashSet<Actividad>();

    /**
     * Constructor vacío requerido por JPA.
     */
    public Socio() {
    }

    /**
     * Constructor con la clave primaria.
     *
     * @param numeroSocio Código del socio.
     */
    public Socio(String numeroSocio) {
        this.numeroSocio = numeroSocio;
    }

    /**
     * Constructor con los campos obligatorios (Non-Null en BD).
     *
     * @param numeroSocio Código del socio.
     * @param nombre Nombre completo.
     * @param dni DNI.
     * @param fechaEntrada Fecha de alta.
     * @param categoria Categoría asignada.
     */
    public Socio(String numeroSocio, String nombre, String dni, String fechaEntrada, Character categoria) {
        this.numeroSocio = numeroSocio;
        this.nombre = nombre;
        this.dni = dni;
        this.fechaEntrada = fechaEntrada;
        this.categoria = categoria;
    }

    /**
     * Constructor completo con todos los atributos.
     *
     * @param numeroSocio Código del socio.
     * @param nombre Nombre completo.
     * @param dni DNI.
     * @param fechaNacimiento Fecha de nacimiento.
     * @param telefono Teléfono.
     * @param correo Correo electrónico.
     * @param fechaEntrada Fecha de alta.
     * @param categoria Categoría.
     */
    public Socio(String numeroSocio, String nombre, String dni, String fechaNacimiento, String telefono, String correo, String fechaEntrada, Character categoria) {
        this.numeroSocio = numeroSocio;
        this.nombre = nombre;
        this.dni = dni;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.correo = correo;
        this.fechaEntrada = fechaEntrada;
        this.categoria = categoria;
    }

    /**
     * @return El número de socio.
     */
    public String getNumeroSocio() {
        return numeroSocio;
    }

    /**
     * @param numeroSocio Nuevo número de socio.
     */
    public void setNumeroSocio(String numeroSocio) {
        this.numeroSocio = numeroSocio;
    }

    /**
     * @return El nombre del socio.
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
     * @return La fecha de nacimiento.
     */
    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * @param fechaNacimiento Nueva fecha de nacimiento.
     */
    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
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
     * @param fechaEntrada Nueva fecha de entrada.
     */
    public void setFechaEntrada(String fechaEntrada) {
        this.fechaEntrada = fechaEntrada;
    }

    /**
     * @return La categoría del socio.
     */
    public Character getCategoria() {
        return categoria;
    }

    /**
     * @param categoria Nueva categoría.
     */
    public void setCategoria(Character categoria) {
        this.categoria = categoria;
    }

    /**
     * @return Conjunto de actividades realizadas.
     */
    public Set<Actividad> getActividadSet() {
        return actividades;
    }

    /**
     * @param actividadSet Nuevo conjunto de actividades.
     */
    public void setActividadSet(Set<Actividad> actividadSet) {
        this.actividades = actividadSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (numeroSocio != null ? numeroSocio.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Socio)) {
            return false;
        }
        Socio other = (Socio) object;
        if ((this.numeroSocio == null && other.numeroSocio != null) || (this.numeroSocio != null && !this.numeroSocio.equals(other.numeroSocio))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Modelo.Socio[ numeroSocio=" + numeroSocio + " ]";
    }
}
