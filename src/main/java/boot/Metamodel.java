package boot;

import builder.dml.EntityMetaData;
import persistence.CollectionPersister;
import persistence.EntityPersister;

import java.util.List;

public interface Metamodel {
    void init();

    EntityMetaData entityMetaData(Class<?> entityClass);

    EntityPersister entityPersister(Class<?> entityClass);

    CollectionPersister collectionPersister(String roll);

    List<String> getEntityClasses();
}
