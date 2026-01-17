package Modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.sql.CallableStatement;
import java.sql.Types;

/**
 * Clase Data Access Object (DAO) para la gestión de Actividades. 
 * Permite realizar operaciones CRUD, búsquedas avanzadas y cálculo de estadísticas.
 * @author Manuel Martín Rodrigo
 */
public class ActividadDAO {

    public ActividadDAO() {
    }

    /**
     * Recupera la lista de socios inscritos en una actividad específica.
     * @param session Sesión de Hibernate activa.
     * @param idActividad Identificador de la actividad.
     * @return Lista de objetos Socio que realizan dicha actividad.
     */
    public List<Socio> obtenerSociosInscritos(Session session, String idActividad) throws Exception {
        Query<Socio> query = session.createQuery(
                "SELECT s FROM Socio s JOIN s.actividades a WHERE a.idActividad = :id",
                Socio.class
        );
        query.setParameter("id", idActividad);
        return query.getResultList();
    }

    /**
     * Busca una actividad por su identificador único (ID).
     * @param session Sesión activa.
     * @param idActividad ID a buscar.
     * @return Objeto Actividad o null.
     */
    public Actividad buscarPorId(Session session, String idActividad) throws Exception {
        return session.find(Actividad.class, idActividad);
    }

    /**
     * Obtiene el listado completo de actividades disponibles.
     * @param session Sesión activa.
     * @return Lista de todas las actividades.
     */
    public List<Actividad> listaActividades(Session session) {
        Query<Actividad> q = session.createQuery("from Actividad", Actividad.class);
        return q.getResultList();
    }

    /**
     * Inserta una nueva actividad en la base de datos.
     * @param session Sesión activa.
     * @param actividad Objeto a guardar.
     */
    public void insertarActividad(Session session, Actividad actividad) throws Exception {
        session.save(actividad);
    }

    /**
     * Elimina una actividad de la base de datos.
     * @param session Sesión activa.
     * @param actividad Objeto a borrar.
     */
    public void borrarActividad(Session session, Actividad actividad) throws Exception {
        session.delete(actividad);
    }

    /**
     * Actualiza los datos de una actividad existente.
     * @param session Sesión activa.
     * @param actividad Objeto con nuevos datos.
     */
    public void actualizarActividad(Session session, Actividad actividad) throws Exception {
        session.update(actividad);
    }

    /**
     * Busca actividades cuyo nombre contenga el texto proporcionado.
     * @param session Sesión activa.
     * @param parteNombre Texto a buscar.
     * @return Lista de actividades coincidentes.
     */
    public List<Actividad> buscarActividadesPorNombre(Session session, String parteNombre) throws Exception {
        String hql = "FROM Actividad a WHERE a.nombre LIKE :nombre";
        Query<Actividad> query = session.createQuery(hql, Actividad.class);
        query.setParameter("nombre", "%" + parteNombre + "%");
        return query.getResultList();
    }

    /**
     * Obtiene el código (ID) más alto registrado en la tabla ACTIVIDAD.
     * Necesario para el cálculo automático de claves (Item 30 del Checklist).
     * @param s Sesión activa.
     * @return El String con el último ID o null si la tabla está vacía.
     */
    public String obtenerUltimoCodigo(Session s) {
        String hql = "SELECT max(a.idActividad) FROM Actividad a";
        Query<String> q = s.createQuery(hql, String.class);
        return q.uniqueResult();
    }

    /**
     * Verifica si un monitor ya tiene asignada una actividad ese mismo día y hora.
     * @param session Sesión activa.
     * @param codMonitor Código del monitor.
     * @param dia Día de la semana.
     * @param hora Hora de la actividad (int).
     * @return true si ya está ocupado, false si está libre.
     */
    public boolean existeChoqueMonitor(Session session, String codMonitor, String dia, int hora) {
        String hql = "SELECT count(a) FROM Actividad a WHERE a.monitorResponsable.codMonitor = :m AND a.dia = :d AND a.hora = :h";
        Query<Long> q = session.createQuery(hql, Long.class);
        q.setParameter("m", codMonitor);
        q.setParameter("d", dia);
        q.setParameter("h", hora);
        return q.uniqueResult() > 0;
    }

    /**
     * Llama al Procedimiento Almacenado 'sp_estadisticas_actividad'.
     * @param session Sesión activa.
     * @param idActividad ID de la actividad.
     * @return Array con las estadísticas.
     */
    public Object[] obtenerEstadisticas(org.hibernate.Session session, String idActividad) {
        return session.doReturningWork(connection -> {
            Object[] resultados = new Object[4];
            String sql = "{call sp_estadisticas_actividad(?, ?, ?, ?, ?)}";

            try (CallableStatement call = connection.prepareCall(sql)) {
                call.setString(1, idActividad);
                call.registerOutParameter(2, Types.INTEGER); // socios
                call.registerOutParameter(3, Types.DOUBLE);  // edad
                call.registerOutParameter(4, Types.CHAR);    // categoria
                call.registerOutParameter(5, Types.DOUBLE);  // ingresos

                call.execute();

                resultados[0] = call.getInt(2);
                resultados[1] = call.getDouble(3);
                resultados[2] = call.getString(4);
                resultados[3] = call.getDouble(5);
            }
            return resultados;
        });
    }
}