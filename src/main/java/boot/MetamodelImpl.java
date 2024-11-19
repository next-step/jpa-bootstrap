package boot;

import builder.dml.EntityMetaData;
import builder.dml.builder.DMLQueryBuilder;
import hibernate.AnnotationBinder;
import jdbc.JdbcTemplate;
import persistence.CollectionPersister;
import persistence.EntityPersister;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetamodelImpl implements Metamodel {

    private static final String DOT = ".";

    private final DMLQueryBuilder dmlQueryBuilder = new DMLQueryBuilder();
    private final Map<String, EntityMetaData> entityMetaDataMap = new HashMap<>();
    private EntityPersister entityPersister;
    private CollectionPersister collectionPersister;

    private final JdbcTemplate jdbcTemplate;

    public MetamodelImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void init() {
        AnnotationBinder annotationBinder = new AnnotationBinder("entity");
        List<Class<?>> entityClasses = annotationBinder.getEntityClasses();

        entityPersister = new EntityPersister(jdbcTemplate, dmlQueryBuilder);
        collectionPersister = new CollectionPersister(jdbcTemplate, dmlQueryBuilder);

        for (Class<?> entityClass : entityClasses) {
            entityMetaDataMap.put(entityClass.getSimpleName(), new EntityMetaData(entityClass));
        }
    }

    @Override
    public EntityMetaData entityMetaData(Class<?> entityClass) {
        return entityMetaDataMap.get(entityClass.getSimpleName());
    }

    @Override
    public EntityPersister entityPersister() {
        return this.entityPersister;
    }

    @Override
    public CollectionPersister collectionPersister() {
        return this.collectionPersister;
    }

}
