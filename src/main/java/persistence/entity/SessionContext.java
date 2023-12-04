package persistence.entity;

import java.util.function.Function;
import persistence.entity.impl.EntityManagerFactory;

public interface SessionContext {

    EntityManager getEntityManager(EntityManagerFactory entityManagerFactory);

    EntityManager bindEntityManager(EntityManagerFactory entityManagerFactory, Function<EntityManagerFactory, EntityManager> generateEntityManagerFunction);
}
