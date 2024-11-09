package persistence.session;

public interface CurrentSessionContext {
    EntityManager currentSession();

    void bindSession(EntityManager session);

    void closeSession();
}
