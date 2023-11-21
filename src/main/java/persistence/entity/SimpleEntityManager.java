package persistence.entity;


import java.sql.Connection;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.entity.persister.EntityPersister;
import persistence.meta.MetaModel;


public class SimpleEntityManager implements EntityManager {
    private final EntityPersisteContext entityPersisterContenxt;
    private final Connection connection;
    private final SimplePersistenceContext persistenceContext;

    private SimpleEntityManager(MetaModel metaModel, Connection connection) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        this.connection = connection;
        this.persistenceContext = new SimplePersistenceContext();
        this.entityPersisterContenxt = EntityPersisteContext.create(metaModel, jdbcTemplate);

    }

    public static SimpleEntityManager create(MetaModel metaModel, Connection connection) {
        return new SimpleEntityManager(metaModel, connection);
    }

    @Override
    public <T> T persist(T entity) {
        EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(entity.getClass());

        return persistenceContext.saving(entityPersister, entity);
    }

    @Override
    public void remove(Object entity) {
        persistenceContext.deleted(entity);
    }

    @Override
    public <T, ID> T find(Class<T> clazz, ID id) {
        EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(clazz);

        return persistenceContext.loading(entityPersister, clazz, id);
    }

    @Override
    public <T> List<T> findAll(Class<T> tClass) {
        EntityPersister entityPersister = entityPersisterContenxt.getEntityPersister(tClass);

        return persistenceContext.findAll(entityPersister, tClass);
    }

    @Override
    public void flush() {
        persistenceContext.flush(entityPersisterContenxt);
    }

}
