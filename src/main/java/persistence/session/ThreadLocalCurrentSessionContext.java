package persistence.session;

public class ThreadLocalCurrentSessionContext implements CurrentSessionContext {

    private static final ThreadLocal<EntityManager> sessionHolder = new ThreadLocal<>();

    @Override
    public EntityManager currentSession() {
        return sessionHolder.get();
    }

    @Override
    public void bindSession(EntityManager session) {
        sessionHolder.set(session);
    }

    @Override
    public void closeSession() {
        final EntityManager session = sessionHolder.get();
        if (session != null) {
            session.clear();
        }
        sessionHolder.remove();
    }
}
