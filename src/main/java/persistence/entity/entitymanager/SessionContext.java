package persistence.entity.entitymanager;

import jdbc.JdbcTemplate;

public interface SessionContext {

    EntityManager currentSession();

    void bindSession(JdbcTemplate jdbcTemplate, EntityManager entityManager);

    void close();
}
