package persistence.entity;

import java.util.function.Function;
import persistence.entity.impl.EntityManagerFactory;

public interface SessionContext {

    EntityManager tryBindEntityManager(EntityManagerFactory entityManagerFactory, Function<EntityManagerFactory, EntityManager> generateEntityManagerFunction);
}
