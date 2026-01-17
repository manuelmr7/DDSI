package Config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.HibernateException;

/**
 * Clase de utilidad para la configuración y gestión de la sesión de Hibernate.
 * Se encarga de establecer la conexión con la base de datos MariaDB y construir la SessionFactory.
 *
 * @author Manuel Martín Rodrigo
 */
public class HibernateUtil {
    private static SessionFactory sessionFactory;
    private static StandardServiceRegistry serviceRegistry;

    /**
     * Construye y devuelve la SessionFactory de Hibernate utilizando las credenciales proporcionadas.
     * Configura dinámicamente la conexión JDBC.
     *
     * @param user Nombre de usuario para la conexión a la base de datos.
     * @param pass Contraseña del usuario.
     * @return La SessionFactory creada o null si ocurre un error.
     */
    public static SessionFactory buildSessionFactory(String user, String pass) {
        try {
            serviceRegistry = new StandardServiceRegistryBuilder()
                .configure("hibernate.cfg.xml") // Carga la configuración base
                .applySetting("hibernate.connection.username", user)
                .applySetting("hibernate.connection.password", pass)
                .applySetting("hibernate.connection.url", 
                    "jdbc:mariadb://172.18.1.241:3306/" + user) // URL dinámica
                .build();

            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
            return sessionFactory;

        } catch (HibernateException e) {
            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
                serviceRegistry = null;
            }
            sessionFactory = null;
            System.err.println("Error al crear la SessionFactory: " + e.getMessage());
            return null;
        }
    }

    /**
     * Obtiene la instancia actual de SessionFactory.
     *
     * @return La SessionFactory activa.
     * @throws IllegalStateException Si la fábrica no ha sido inicializada previamente.
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            throw new IllegalStateException("La SessionFactory aún no está inicializada. " +
                "Debe llamar al método buildSessionFactory() primero.");
        }
        return sessionFactory;
    }

    /**
     * Cierra la SessionFactory y libera los recursos del registro de servicios.
     */
    public static void close() {
        try {
            if (sessionFactory != null && !sessionFactory.isClosed()) {
                sessionFactory.close();
            }
        } finally {
            sessionFactory = null;
            if (serviceRegistry != null) {
                StandardServiceRegistryBuilder.destroy(serviceRegistry);
                serviceRegistry = null;
            }
        }
    }
}