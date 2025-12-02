package Modelo;

import java.util.List;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class SocioDAO {
    public SocioDAO() {}
    
    public void insertaSocio(Session session, Socio socio) throws Exception {
        session.persist(socio);
    }
    
    public Socio buscarPorNumeroSocio(Session session, String numeroSocio) throws Exception {
        return session.find(Socio.class, numeroSocio);
    }
    
    public Socio buscarPorDni(Session session, String dni) throws Exception {
        Query<Socio> query = session.createQuery(
            "FROM Socio s WHERE s.dni = :dni", 
            Socio.class
        );
        query.setParameter("dni", dni);
        return query.uniqueResult();
    }
    
    public boolean existeSocio(Session session, String numeroSocio, String dni) throws Exception {
        Query<Long> query = session.createQuery(
            "SELECT COUNT(s) FROM Socio s WHERE s.numeroSocio = :numero OR s.dni = :dni", 
            Long.class
        );
        query.setParameter("numero", numeroSocio);
        query.setParameter("dni", dni);
        return query.getSingleResult() > 0;
    }
    public List<Socio> listaSocios(Session session) {
        Query<Socio> q = session.createQuery("from Socio", Socio.class);
        return q.getResultList();
    }
}
