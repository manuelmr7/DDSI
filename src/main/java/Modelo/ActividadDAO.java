package Modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.sql.CallableStatement;
import java.sql.Types;

/**
 * Clase DAO para la gestión de Actividades. Permite realizar operaciones CRUD,
 * búsquedas avanzadas y cálculo de estadísticas.
 * @author manue
 */
public class ActividadDAO {

    public ActividadDAO() {
    }

    /**
     * Recupera la lista de socios inscritos en una actividad específica.
     *
     * @param session Sesión de Hibernate activa.
     * @param idActividad Identificador de la actividad.
     * @return Lista de objetos Socio que realizan dicha actividad.
     * @throws Exception Si ocurre un error en la consulta.
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
     *
     * @param session Sesión de Hibernate activa.
     * @param idActividad ID de la actividad a buscar.
     * @return El objeto Actividad encontrado o null.
     * @throws Exception Si ocurre un error.
     */
    public Actividad buscarPorId(Session session, String idActividad) throws Exception {
        return session.find(Actividad.class, idActividad);
    }

    /**
     * Obtiene el listado completo de actividades disponibles.
     *
     * @param session Sesión de Hibernate activa.
     * @return Lista de todas las actividades.
     */
    public List<Actividad> listaActividades(Session session) {
        Query<Actividad> q = session.createQuery("from Actividad", Actividad.class);
        return q.getResultList();
    }

    /**
     * Inserta una nueva actividad en la base de datos.
     *
     * @param session Sesión de Hibernate activa.
     * @param actividad Objeto Actividad a guardar.
     * @throws Exception Si ocurre un error al guardar.
     */
    public void insertarActividad(Session session, Actividad actividad) throws Exception {
        session.save(actividad);
    }

    /**
     * Elimina una actividad de la base de datos.
     *
     * @param session Sesión de Hibernate activa.
     * @param actividad Objeto Actividad a borrar.
     * @throws Exception Si ocurre un error al borrar.
     */
    public void borrarActividad(Session session, Actividad actividad) throws Exception {
        session.delete(actividad);
    }

    /**
     * Actualiza los datos de una actividad existente.
     *
     * @param session Sesión de Hibernate activa.
     * @param actividad Objeto Actividad con los nuevos datos.
     * @throws Exception Si ocurre un error al actualizar.
     */
    public void actualizarActividad(Session session, Actividad actividad) throws Exception {
        session.update(actividad);
    }

    /**
     * Busca actividades cuyo nombre contenga el texto proporcionado (filtro
     * parcial).
     *
     * @param session Sesión de Hibernate activa.
     * @param parteNombre Texto a buscar dentro del nombre.
     * @return Lista de actividades coincidentes.
     * @throws Exception Si ocurre un error en la consulta.
     */
    public List<Actividad> buscarActividadesPorNombre(Session session, String parteNombre) throws Exception {
        String hql = "FROM Actividad a WHERE a.nombre LIKE :nombre";
        Query<Actividad> query = session.createQuery(hql, Actividad.class);
        query.setParameter("nombre", "%" + parteNombre + "%");
        return query.getResultList();
    }

    /**
     * Llama al Procedimiento Almacenado 'sp_estadisticas_actividad' para
     * obtener métricas.
     *
     * @param session Sesión de Hibernate activa.
     * @param idActividad ID de la actividad a analizar.
     * @return Array de objetos: [0]=NumSocios, [1]=EdadMedia,
     * [2]=CategoriaFrecuente, [3]=Ingresos.
     */
    public Object[] obtenerEstadisticas(org.hibernate.Session session, String idActividad) {
        return session.doReturningWork(connection -> {
            Object[] resultados = new Object[4];
            // Llamada JDBC al procedimiento: 1 parámetro de entrada (IN) y 4 de salida (OUT)
            String sql = "{call sp_estadisticas_actividad(?, ?, ?, ?, ?)}";

            try (CallableStatement call = connection.prepareCall(sql)) {
                call.setString(1, idActividad);

                // Registramos los tipos de datos de salida
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
