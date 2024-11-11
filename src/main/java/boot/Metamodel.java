package boot;

import persistence.CollectionPersister;
import persistence.EntityLoader;
import persistence.EntityPersister;

import java.util.List;

public interface Metamodel {
    void init();

    EntityLoader entityLoader(Class<?> entityClass);

    EntityPersister entityPersister(Class<?> entityClass);

    CollectionPersister collectionPersister(String roll);

    List<String> getEntityClasses();
}
