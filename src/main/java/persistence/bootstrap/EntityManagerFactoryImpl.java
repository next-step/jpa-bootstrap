package persistence.bootstrap;

import persistence.entitymanager.EntityManager;
import persistence.entitymanager.EntityManagerImpl;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final Metadata metadata;
    private final Metamodel metamodel;
//    private SessionContext sessionContext;

    public EntityManagerFactoryImpl(Metadata metadata, Metamodel metamodel) {
        this.metadata = metadata;
        this.metamodel = metamodel;
//        this.sessionContext = null;
    }

    public void initialize() {
//        this.sessionContext = new SessionContext(
//                metadata,
//                jdbcTemplate,
//                dialect
//        );
//        sessionContext.initialize();
    }

    @Override
    public EntityManager openSession() {
//        Session session = sessionContext.createSession(metamodel);
        return EntityManagerImpl.newEntityManager(metamodel, metadata);
    }
}
