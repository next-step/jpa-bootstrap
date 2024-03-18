package repository;

import persistence.entity.EntityManager;
import persistence.entity.EntityMeta;

public class CustomJpaRepository<T, ID> implements JpaRepository<T, ID> {
    private final EntityManager entityManager;
    private final EntityMeta<T> entityMeta;

    public CustomJpaRepository(EntityManager entityManager, EntityMeta<T> entityMeta) {
        this.entityManager = entityManager;
        this.entityMeta = entityMeta;
    }

    public T save(T entity) {
        if (entityMeta.isNew(entity)) {
            return entityManager.persist(entity);
        }
        return entityManager.merge(entity);
    }
}
