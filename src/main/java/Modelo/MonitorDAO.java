package Modelo;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;

public class MonitorDAO {
    public MonitorDAO() {}
    
    public List<Actividad> obtenerActividadesPorMonitor(Session session, String dniMonitor) throws Exception {
        Query<Actividad> query = session.createQuery(
            "FROM Actividad a WHERE a.monitorResponsable.dni = :dni", 
            Actividad.class
        );
        query.setParameter("dni", dniMonitor);
        return query.getResultList();
    }
    
    public Monitor buscarPorDni(Session session, String dni) throws Exception {
        Query<Monitor> query = session.createQuery(
            "FROM Monitor m WHERE m.dni = :dni", 
            Monitor.class
        );
        query.setParameter("dni", dni);
        return query.uniqueResult();
    }
    public List<Monitor> listaMonitores(Session session) {
        // HQL: Selecciona todos los objetos de la clase Monitor
        Query<Monitor> q = session.createQuery("from Monitor", Monitor.class);
        return q.getResultList();
    }
    public Monitor buscarPorCodMonitor(Session session, String codMonitor)
    {
        return session.get(Monitor.class,codMonitor);
    }
    public void insertarMonitor(Session session,Monitor monitor) throws Exception
    {
        session.save(monitor);
    }
    public void borrarMonitor(Session session,Monitor monitor) throws Exception
    {
        session.delete(monitor);
    }
    public void actualizarMonitor(Session session,Monitor monitor) throws Exception
    {
        session.update(monitor);
    }
    public String obtenerUltimoCodigo(Session session)
    {
        Query<String> q=session.createQuery("SELECT max(m.codMonitor) FROM Monitor m",String.class);
        return q.getSingleResult();
    }
}
