package Modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

/**
 * Clase Data Access Object (DAO) para la gestión de la entidad Monitor.
 * Se encarga de todas las operaciones de persistencia (CRUD) y consultas
 * contra la tabla MONITOR de la base de datos.
 *
 * @author Manuel Martín Rodrigo
 */
public class MonitorDAO {

    /**
     * Constructor por defecto.
     */
    public MonitorDAO() {
    }
    
    /**
     * Recupera todas las actividades asignadas a un monitor específico buscado por su DNI.
     * * @param session Sesión de Hibernate activa.
     * @param dniMonitor DNI del monitor responsable.
     * @return Lista de actividades asociadas a ese monitor.
     * @throws Exception Si ocurre un error en la consulta HQL.
     */
    public List<Actividad> obtenerActividadesPorMonitor(Session session, String dniMonitor) throws Exception {
        Query<Actividad> query = session.createQuery(
            "FROM Actividad a WHERE a.monitorResponsable.dni = :dni", 
            Actividad.class
        );
        query.setParameter("dni", dniMonitor);
        return query.getResultList();
    }
    
    /**
     * Busca un objeto Monitor en la base de datos a partir de su DNI.
     * * @param session Sesión de Hibernate activa.
     * @param dni DNI a buscar.
     * @return El objeto Monitor encontrado o null si no existe.
     * @throws Exception Si ocurre un error durante la búsqueda.
     */
    public Monitor buscarPorDni(Session session, String dni) throws Exception {
        Query<Monitor> query = session.createQuery(
            "FROM Monitor m WHERE m.dni = :dni", 
            Monitor.class
        );
        query.setParameter("dni", dni);
        return query.uniqueResult();
    }

    /**
     * Obtiene el listado completo de todos los monitores registrados.
     * * @param session Sesión de Hibernate activa.
     * @return Lista de objetos Monitor.
     */
    public List<Monitor> listaMonitores(Session session) {
        Query<Monitor> q = session.createQuery("from Monitor", Monitor.class);
        return q.getResultList();
    }

    /**
     * Busca un monitor por su clave primaria (Código de Monitor).
     * * @param session Sesión de Hibernate activa.
     * @param codMonitor Código único del monitor (ej: M001).
     * @return El objeto Monitor correspondiente o null.
     */
    public Monitor buscarPorCodMonitor(Session session, String codMonitor) {
        return session.get(Monitor.class, codMonitor);
    }

    /**
     * Inserta un nuevo registro de monitor en la base de datos.
     * * @param session Sesión de Hibernate con transacción activa.
     * @param monitor Objeto Monitor con los datos a persistir.
     * @throws Exception Si falla el guardado (ej: clave duplicada).
     */
    public void insertarMonitor(Session session, Monitor monitor) throws Exception {
        session.save(monitor);
    }

    /**
     * Elimina un monitor existente de la base de datos.
     * * @param session Sesión de Hibernate con transacción activa.
     * @param monitor Objeto Monitor a eliminar.
     * @throws Exception Si falla el borrado (ej: violación de integridad referencial si tiene actividades).
     */
    public void borrarMonitor(Session session, Monitor monitor) throws Exception {
        session.delete(monitor);
    }

    /**
     * Actualiza los datos de un monitor existente.
     * * @param session Sesión de Hibernate con transacción activa.
     * @param monitor Objeto Monitor con la información actualizada.
     * @throws Exception Si ocurre un error al actualizar.
     */
    public void actualizarMonitor(Session session, Monitor monitor) throws Exception {
        session.update(monitor);
    }

    /**
     * Obtiene el código de monitor más alto registrado actualmente (ej: "M015").
     * Utilizado para calcular automáticamente el siguiente código disponible.
     * * @param session Sesión de Hibernate activa.
     * @return El String con el último código o null si la tabla está vacía.
     */
    public String obtenerUltimoCodigo(Session session) {
        Query<String> q = session.createQuery("SELECT max(m.codMonitor) FROM Monitor m", String.class);
        return q.getSingleResult();
    }
}