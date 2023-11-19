package persistence.entity.manager;

import database.DatabaseServer;

import java.sql.SQLException;

public interface EntityManagerFactory {
    EntityManager openSession(DatabaseServer databaseServer) throws SQLException;

    EntityManager getSession(DatabaseServer databaseServer) throws SQLException;
}
