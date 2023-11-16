package persistence.entity;

import jdbc.JdbcTemplate;

import java.sql.Connection;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final CurrentSessionContext currentSessionContext;

    private final Connection connection;

    private EntityManagerFactoryImpl(CurrentSessionContext currentSessionContext, Connection connection) {
        this.currentSessionContext = currentSessionContext;
        this.connection = connection;
    }

    public static EntityManagerFactory of(Connection connection) {
        DefaultCurrentSessionContext sessionContext = DefaultCurrentSessionContext.getInstance();
        return new EntityManagerFactoryImpl(sessionContext, connection);
    }

    @Override
    public EntityManager openSession() {
        EntityManager entityManager = currentSessionContext.currentSession();
        if (entityManager != null) {
            throw new IllegalStateException("세션 생성이 완료되었습니다.");
        }
        DefaultEntityManager defaultEntityManager = DefaultEntityManager.of(new JdbcTemplate(connection));
        currentSessionContext.openSession(Thread.currentThread(), defaultEntityManager);
        return defaultEntityManager;
    }

}
