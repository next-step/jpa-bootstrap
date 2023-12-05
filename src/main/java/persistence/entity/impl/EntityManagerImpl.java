package persistence.entity.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityManager;
import persistence.entity.EventSource;
import persistence.entity.PersistenceContext;
import persistence.entity.impl.event.EntityEventPublisher;
import persistence.entity.impl.event.type.DeleteEntityEvent;
import persistence.entity.impl.event.type.LoadEntityEvent;
import persistence.entity.impl.event.type.MergeEntityEvent;
import persistence.entity.impl.event.type.PersistEntityEvent;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;
import registry.EntityMetaRegistry;

public class EntityManagerImpl implements EntityManager {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Connection connection;
    private final PersistenceContext persistenceContext;
    private final EntityEventPublisher entityEventPublisher;
    private final EntityMetaRegistry entityMetaRegistry;

    private EntityManagerImpl(Connection connection, PersistenceContext persistenceContext,
        EntityEventPublisher eventPublisher, EntityMetaRegistry entityMetaRegistry) {
        this.connection = connection;
        this.entityEventPublisher = eventPublisher;
        this.persistenceContext = persistenceContext;
        this.entityMetaRegistry = entityMetaRegistry;
    }

    public static EntityManager of(Connection connection, PersistenceContext persistenceContext,
        EntityEventPublisher eventPublisher, EntityMetaRegistry entityMetaRegistry) {

        return new EntityManagerImpl(connection, persistenceContext, eventPublisher, entityMetaRegistry);
    }

    @Override
    public <T> T find(Class<T> clazz, Object id) {
        final Optional<Object> cachedEntity = persistenceContext.getEntity(clazz, id);
        if (cachedEntity.isEmpty()) {
            final Object loadedEntity = entityEventPublisher.onLoad(LoadEntityEvent.of(clazz, id, (EventSource) persistenceContext));
            return clazz.cast(loadedEntity);
        }

        return clazz.cast(cachedEntity.get());
    }

    @Override
    public Object persist(Object entity) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(entity.getClass());
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, entityClassMappingMeta);

        final Optional<Object> cachedEntity = persistenceContext.getEntity(entity.getClass(), objectMappingMeta.getIdValue());

        return cachedEntity.orElseGet(() ->
            entityEventPublisher.onPersist(PersistEntityEvent.of(entity, (EventSource) persistenceContext))
        );
    }

    @Override
    public void remove(Object entity) {
        entityEventPublisher.onDelete(DeleteEntityEvent.of(entity, (EventSource) persistenceContext));
    }

    @Override
    public <T> T merge(Class<T> clazz, T entity) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(clazz);
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(entity, entityClassMappingMeta);

        final SnapShot snapShot = persistenceContext.getSnapShot(entity.getClass(), objectMappingMeta.getIdValue());
        if (snapShot.isSameWith(objectMappingMeta, entityClassMappingMeta)) {
            return entity;
        }

        final Object mergedEntity = entityEventPublisher.onMerge(MergeEntityEvent.of(entity, (EventSource) persistenceContext));

        return clazz.cast(mergedEntity);
    }

    @Override
    public void clear() {
        persistenceContext.clearContextCache();
    }

    @Override
    public void close() {
        try {
            if (this.connection == null) {
                return;
            }

            if (this.connection.isClosed()) {
                return;
            }

            this.clear();
            this.connection.close();
            log.info("EntityManager closed");
        } catch (SQLException e) {
            log.error("EntityManager connection not closed", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public EntityClassMappingMeta getEntityMeta(Class<?> clazz) {
        return this.entityMetaRegistry.getEntityMeta(clazz);
    }
}
