package hibernate;

import jdbc.JdbcTemplate;
import persistence.EntityManager;

public interface EntityManagerFactory {

    EntityManager openSession();

    void closeSession();

    JdbcTemplate getJdbcTemplate();

}
