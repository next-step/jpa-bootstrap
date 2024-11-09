package persistence.session;

import java.sql.SQLException;

public interface EntityManagerFactory {
    EntityManager openSession() throws SQLException;
}
