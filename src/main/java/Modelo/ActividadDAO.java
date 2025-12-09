package Modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class ActividadDAO {
    public ActividadDAO() {}
    
    public List<Socio> obtenerSociosInscritos(Session session, String idActividad) throws Exception {
        Query<Socio> query = session.createQuery(
            "SELECT s FROM Socio s JOIN s.actividades a WHERE a.idActividad = :id", 
            Socio.class
        );
        query.setParameter("id", idActividad);
        return query.getResultList();
    }
    
    public Actividad buscarPorId(Session session, String idActividad) throws Exception {
        return session.find(Actividad.class, idActividad);
    }
    public List<Actividad> listaActividades(Session session) {
        Query<Actividad> q = session.createQuery("from Actividad", Actividad.class);
        return q.getResultList();
    }
    public void insertarActividad(Session session,Actividad actividad) throws Exception
    {
        session.save(actividad);
    }
    public void borrarActividad(Session session,Actividad actividad) throws Exception
    {
        session.delete(actividad);
    }
    public void actualizarActividad(Session session,Actividad actividad) throws Exception
    {
        session.update(actividad);
    }
}
