package persistence.entitymanager;

import persistence.bootstrap.Metadata;
import persistence.bootstrap.Metamodel;
import persistence.entity.context.EntityEntries;
import persistence.entity.context.FirstLevelCache;
import persistence.entity.context.PersistenceContext;
import persistence.entity.context.PersistenceContextImpl;

public class EntityManagerImpl extends AbstractEntityManager {
    private final Metadata metadata;
    private final PersistenceContext persistenceContext;

    public static EntityManager newEntityManager(Metamodel metamodel, Metadata metadata) {
        return new EntityManagerImpl(metadata, metamodel);
    }

    private EntityManagerImpl(Metadata metadata, Metamodel metamodel) {
        super(metamodel);

        this.metadata = metadata;
        this.persistenceContext = new PersistenceContextImpl(
                metadata,
                new FirstLevelCache(),
                new EntityEntries(),
                this);
    }

    @Override
    public <T> T find(Class<T> entityClass, Long id) {
        return persistenceContext.getEntity(metadata.getPersistentClass(entityClass), id);
    }

    @Override
    public void persist(Object entity) {
        persistenceContext.addEntity(entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.removeEntity(entity);
    }
}
