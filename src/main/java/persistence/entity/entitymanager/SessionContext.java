package persistence.entity.entitymanager;

import jdbc.JdbcTemplate;

public interface SessionContext {

    EntityManager currentSession();

    void bindSession(EntityManager entityManager);

    void close();
}
