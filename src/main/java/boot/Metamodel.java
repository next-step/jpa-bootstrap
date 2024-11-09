package boot;

import persistence.CollectionPersister;
import persistence.EntityPersister;

import java.util.List;

public interface Metamodel {
    void init();

    EntityPersister entityPersister(Class<?> entityClass);

    CollectionPersister collectionPersister(String roll);

    List<String> getEntityClasses();
}
