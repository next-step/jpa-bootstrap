package persistence.entity;

public interface CurrentSessionContext {

    void openSession(Thread thread, EntityManager entityManager);

    EntityManager currentSession();
}
