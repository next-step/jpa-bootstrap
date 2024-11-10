package persistence.session;

import java.sql.SQLException;

public interface EntityManagerFactory extends AutoCloseable {
    EntityManager openSession() throws SQLException;

    void close() throws SQLException;
}
