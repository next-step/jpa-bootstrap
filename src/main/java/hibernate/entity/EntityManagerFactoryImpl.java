package hibernate.entity;

import hibernate.metamodel.BasicMetaModel;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final CurrentSessionContext currentSessionContext;
    private final BasicMetaModel basicMetaModel;

    public EntityManagerFactoryImpl(CurrentSessionContext currentSessionContext, BasicMetaModel basicMetaModel) {
        this.currentSessionContext = currentSessionContext;
        this.basicMetaModel = basicMetaModel;
    }

    @Override
    public EntityManager openSession() {
        EntityManager entityManager = currentSessionContext.currentSession();
        if (entityManager != null) {
            throw new IllegalStateException("이미 현재 스레드에 생성된 EntityManager가 있습니다.");
        }
        return null;
    }
}
