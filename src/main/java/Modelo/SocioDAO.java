package Modelo;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

/**
 * Clase Data Access Object (DAO) para la gestión de Socios. Maneja todas las
 * operaciones CRUD y consultas relacionadas con la tabla SOCIO.
 * @author manue
 */
public class SocioDAO {

    public SocioDAO() {
    }

    /**
     * Inserta un nuevo socio en la base de datos.
     *
     * * @param session Sesión de Hibernate activa.
     * @param socio Objeto Socio a persistir.
     * @throws Exception Si ocurre un error durante la inserción.
     */
    public void insertaSocio(Session session, Socio socio) throws Exception {
        session.persist(socio);
    }

    /**
     * Busca un socio por su clave primaria (Número de socio).
     *
     * * @param session Sesión de Hibernate activa.
     * @param numeroSocio Código del socio (ej: S001).
     * @return El objeto Socio si existe, o null si no se encuentra.
     * @throws Exception Si ocurre un error en la consulta.
     */
    public Socio buscarPorNumeroSocio(Session session, String numeroSocio) throws Exception {
        return session.find(Socio.class, numeroSocio);
    }

    /**
     * Busca un socio por su DNI.
     *
     * * @param session Sesión de Hibernate activa.
     * @param dni DNI del socio a buscar.
     * @return El objeto Socio encontrado o null.
     * @throws Exception Si hay error en la consulta.
     */
    public Socio buscarPorDni(Session session, String dni) throws Exception {
        Query<Socio> query = session.createQuery(
                "FROM Socio s WHERE s.dni = :dni",
                Socio.class
        );
        query.setParameter("dni", dni);
        return query.uniqueResult();
    }

    /**
     * Comprueba si existe un socio con ese número o DNI. Útil para validaciones
     * antes de insertar.
     *
     * * @param session Sesión de Hibernate.
     * @param numeroSocio Código del socio.
     * @param dni DNI del socio.
     * @return true si existe alguna coincidencia, false en caso contrario.
     * @throws Exception Si ocurre un error en la consulta.
     */
    public boolean existeSocio(Session session, String numeroSocio, String dni) throws Exception {
        Query<Long> query = session.createQuery(
                "SELECT COUNT(s) FROM Socio s WHERE s.numeroSocio = :numero OR s.dni = :dni",
                Long.class
        );
        query.setParameter("numero", numeroSocio);
        query.setParameter("dni", dni);
        return query.getSingleResult() > 0;
    }

    /**
     * Recupera la lista completa de socios registrados.
     *
     * * @param session Sesión de Hibernate.
     * @return Lista de objetos Socio.
     */
    public List<Socio> listaSocios(Session session) {
        Query<Socio> q = session.createQuery("from Socio", Socio.class);
        return q.getResultList();
    }

    /**
     * Elimina un socio de la base de datos.
     *
     * * @param session Sesión de Hibernate con transacción activa.
     * @param socio Objeto Socio a eliminar.
     * @throws Exception Si ocurre un error al borrar.
     */
    public void borrarSocio(Session session, Socio socio) throws Exception {
        session.delete(socio);
    }

    /**
     * Actualiza los datos de un socio existente.
     *
     * * @param session Sesión de Hibernate con transacción activa.
     * @param socio Objeto Socio con los datos modificados.
     * @throws Exception Si ocurre un error al actualizar.
     */
    public void actualizarSocio(Session session, Socio socio) throws Exception {
        session.update(socio);
    }

    /**
     * Obtiene el último código de socio registrado (ej: S015) para calcular el
     * siguiente.
     *
     * * @param session Sesión de Hibernate.
     * @return El código más alto encontrado en la tabla.
     */
    public String obtenerUltimoCodigo(Session session) {
        Query<String> q = session.createQuery("SELECT max(s.numeroSocio) FROM Socio s", String.class);
        return q.getSingleResult();
    }
}
