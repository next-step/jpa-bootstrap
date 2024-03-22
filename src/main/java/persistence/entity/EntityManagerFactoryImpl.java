package persistence.entity;

import bootstrap.MetaModel;
import bootstrap.MetaModelImpl;
import database.HibernateEnvironment;
import jdbc.JdbcTemplate;
import persistence.session.CurrentSessionContext;
import persistence.session.ThreadSessionContext;

public class EntityManagerFactoryImpl implements EntityManagerFactory {
    private final CurrentSessionContext currentSessionContext;
    private final JdbcTemplate jdbcTemplate;
    private final HibernateEnvironment environment;
    private final MetaModel metaModel;

    public EntityManagerFactoryImpl(HibernateEnvironment environment) {
        this.currentSessionContext = new ThreadSessionContext();
        this.jdbcTemplate = new JdbcTemplate(environment.getConnection());
        this.metaModel = new MetaModelImpl(jdbcTemplate, environment.getDialect(), "domain");
        this.environment = environment;
    }

    @Override
    public EntityManager createEntityManager() {
        validateOpenSession();
        currentSessionContext.bindEntityManager(EntityManagerImpl.of(environment.getDialect(), metaModel));
        return currentEntityManager();
    }

    private void validateOpenSession() {
        if (currentSessionContext.currentEntityManager() != null) {
            throw new IllegalStateException("이미 오픈된 세션이 존재합니다.");
        }
    }

    @Override
    public EntityManager currentEntityManager() {
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
