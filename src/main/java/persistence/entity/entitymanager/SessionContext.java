package persistence.entity.entitymanager;

import java.sql.Connection;

public interface SessionContext {

    EntityManager currentSession();

    void bindSession(Connection connection, EntityManager entityManager);

    void close();
}
