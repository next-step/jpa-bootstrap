package persistence.entity.entitymanager;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

public class ThreadLocalSessionContext implements SessionContext, Closeable {
    private static final ThreadLocal<Map.Entry<Connection, EntityManager>> SESSION = new ThreadLocal<>();

    private ThreadLocalSessionContext() {}

    public static ThreadLocalSessionContext getInstance() {
        return new ThreadLocalSessionContext();
    }

    @Override
    public EntityManager currentSession() {
        return SESSION.get().getValue();
    }

    @Override
    public void bindSession(Connection connection, EntityManager entityManager) {
        SESSION.set(Map.entry(connection, entityManager));
    }

    @Override
    public void close() {
        try {
            SESSION.get().getKey().close();
            SESSION.remove();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
