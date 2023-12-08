package persistence.entity;

import java.sql.SQLException;

public interface EntityManagerFactory {

  EntityManager openSession() throws SQLException;

  void closeSession();
}
