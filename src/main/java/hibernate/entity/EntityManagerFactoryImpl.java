package hibernate.entity;

import hibernate.action.ActionQueue;
import hibernate.entity.persistencecontext.SimplePersistenceContext;
import hibernate.event.EventListenerRegistry;
import hibernate.metamodel.BasicMetaModel;
import hibernate.metamodel.MetaModel;
import hibernate.metamodel.MetaModelImpl;
import jdbc.JdbcTemplate;

import java.sql.Connection;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final CurrentSessionContext currentSessionContext;
    private final BasicMetaModel basicMetaModel;
    private final Connection connection;

    public EntityManagerFactoryImpl(
            final CurrentSessionContext currentSessionContext,
            final BasicMetaModel basicMetaModel,
            final Connection connection
    ) {
        this.currentSessionContext = currentSessionContext;
        this.basicMetaModel = basicMetaModel;
        this.connection = connection;
    }

    @Override
    public EntityManager openSession() {
        EntityManager entityManager = currentSessionContext.currentSession();
        if (entityManager != null) {
            throw new IllegalStateException("이미 현재 스레드에 생성된 EntityManager가 있습니다.");
        }
        MetaModel metaModel = MetaModelImpl.createPackageMetaModel(basicMetaModel, new JdbcTemplate(connection));
        ActionQueue actionQueue = new ActionQueue();
        return new EntityManagerImpl(
                new SimplePersistenceContext(),
                metaModel,
                EventListenerRegistry.createDefaultRegistry(metaModel, actionQueue),
                actionQueue
        );
    }

    @Override
    public EntityManager currentSession() {
        EntityManager entityManager = currentSessionContext.currentSession();
        if (entityManager == null) {
            throw new IllegalStateException("현재 스레드에 생성된 EntityManager가 없습니다.");
        }
        return entityManager;
    }
}
