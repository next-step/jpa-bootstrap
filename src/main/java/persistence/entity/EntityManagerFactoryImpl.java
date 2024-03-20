package persistence.entity;

import database.HibernateProperties;
import jdbc.JdbcTemplate;
import persistence.session.CurrentSessionContext;
import persistence.session.ThreadSessionContext;

public class EntityManagerFactoryImpl implements EntityManagerFactory {
    private final CurrentSessionContext currentSessionContext;
    private final JdbcTemplate jdbcTemplate;
    private final HibernateProperties properties;

    public EntityManagerFactoryImpl(HibernateProperties properties) {
        this.currentSessionContext = new ThreadSessionContext();
        this.jdbcTemplate = new JdbcTemplate(properties.getConnection());
        this.properties = properties;
    }

    @Override
    public EntityManager openSession() {
        validateOpenSession();
        currentSessionContext.bindEntityManager(new EntityManagerImpl(jdbcTemplate, properties.getDialect()));
        return currentSession();
    }

    private void validateOpenSession() {
        if (currentSessionContext.currentEntityManager() != null) {
            throw new IllegalStateException("이미 오픈된 세션이 존재합니다.");
        }
    }

    @Override
    public EntityManager currentSession() {
        EntityManager entityManager = currentSessionContext.currentEntityManager();
        validateCurrentSession(entityManager);
        return entityManager;
    }

    private void validateCurrentSession(EntityManager entityManager) {
        if(entityManager == null) {
            throw new IllegalStateException("현재 오픈된 세션이 없습니다.");
        }
    }
}
