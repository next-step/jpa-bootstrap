package boot;

import builder.dml.EntityMetaData;
import persistence.CollectionPersister;
import persistence.EntityPersister;

import java.util.List;

public interface Metamodel {
    void init();

    EntityMetaData entityMetaData(Class<?> entityClass);

    EntityPersister entityPersister();

    CollectionPersister collectionPersister();

}
