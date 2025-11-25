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
}
