package persistence.entitymanager;

public interface EntityManager {

    <T> T find(Class<T> clazz, Long Id);

    <T> T persist(Object entity);

    void remove(Object entity);

    <T> void createTable(Class<T> clazz);

    <T> void dropTable(Class<T> clazz, boolean ifExists);
}
