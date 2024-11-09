package persistence.session;

public interface CurrentSessionContext {
    EntityManager currentSession();
}
