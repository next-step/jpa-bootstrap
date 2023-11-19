package persistence.entity;

import jdbc.JdbcTemplate;

import java.sql.Connection;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

    private final Connection connection;

    private EntityManagerFactoryImpl(Connection connection) {
        this.connection = connection;
    }

    public static EntityManagerFactory of(Connection connection) {
        return new EntityManagerFactoryImpl(connection);
    }

    @Override
    public EntityManager openSession() {
        EntityManager entityManager = CurrentSessionContext.currentSession();
        if (entityManager != null) {
            throw new IllegalStateException("세션 생성이 완료되었습니다.");
        }
        DefaultEntityManager defaultEntityManager = DefaultEntityManager.of(new JdbcTemplate(connection));
        CurrentSessionContext.openSession(defaultEntityManager);
        return defaultEntityManager;
    }

}
