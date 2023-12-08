package persistence.entity;

import java.sql.SQLException;
import persistence.entity.entry.JdbcEntityEntry;
import persistence.entity.persistentcontext.JdbcPersistenceContext;
import persistence.meta.model.MetaModel;

public class EntityManagerFactoryImpl implements EntityManagerFactory {

  private final SessionContext currentSessionContext = new CurrentSessionContext();
  private final MetaModel metaModel;

  public EntityManagerFactoryImpl(MetaModel metaModel) {
    this.metaModel = metaModel;
  }

  @Override
  public EntityManager openSession() {

    EntityManager entityManager = currentSessionContext.getEntityManager()
        .orElseGet(() ->
            new JdbcEntityManager(new JdbcPersistenceContext(),
                new JdbcEntityEntry(), metaModel)
        );
    currentSessionContext.setEntityManager(entityManager);

    return entityManager;
  }

  @Override
  public void closeSession() {
    currentSessionContext.clearEntityManager();
  }


}
