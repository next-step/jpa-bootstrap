package persistence.entity.manager;


import persistence.context.PersistenceContext;

public interface EntityManager {

    <T> T findById(Class<T> clazz, String Id);

    <T> T persist(T entity);

    <T> void remove(T entity);

    <T> void flush();

    PersistenceContext getPersistenceContext();
}
