package persistence.entity;

public interface CurrentSessionContext {
    void bind(EntityManager entityManager);

    EntityManager currentSession();

    void closeCurrentSession();

}
