package persistence.entity.manager;

import java.sql.SQLException;

public interface EntityManagerFactory {
    void openSession() throws SQLException;

    EntityManager getSession() throws SQLException;
}
