package persistence.entity.entitymanager;

import java.io.Closeable;
import java.util.Map;
import jdbc.JdbcTemplate;
import jdbc.JdbcTemplatePool;

public class ThreadLocalSessionContext implements SessionContext, Closeable {
    private static final ThreadLocal<Map.Entry<JdbcTemplate, EntityManager>> SESSION = new ThreadLocal<>();

    private ThreadLocalSessionContext() {
    }
    private static class Holder {
        static final ThreadLocalSessionContext INSTANCE = new ThreadLocalSessionContext();
    }

    public static ThreadLocalSessionContext getInstance() {
        return ThreadLocalSessionContext.Holder.INSTANCE;
    }


    @Override
    public EntityManager currentSession() {
        return SESSION.get().getValue();
    }

    @Override
    public void bindSession(JdbcTemplate jdbcTemplate, EntityManager entityManager) {
        SESSION.set(Map.entry(jdbcTemplate, entityManager));
    }

    @Override
    public void close() {
        JdbcTemplatePool.releaseJdbcTemplate(SESSION.get().getKey());
        SESSION.remove();
    }
}
