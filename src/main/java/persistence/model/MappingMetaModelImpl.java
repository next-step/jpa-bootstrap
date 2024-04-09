package persistence.model;

import jdbc.JdbcTemplate;
import persistence.entity.Proxy.ProxyFactory;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SingleEntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.EntityPersisterConcurrentMap;
import persistence.entity.persister.SingleTableEntityPersister;
import persistence.model.scan.EntityScanner;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.mapping.TableBinder;

public class MappingMetaModelImpl implements MetaModel {

    private String packagePrefix = "persistence";

    private final PersistentClassMapping persistentClassMapping = new PersistentClassMapping();
    private final EntityPersisterConcurrentMap entityPersisterMap = new EntityPersisterConcurrentMap();
    private EntityLoader entityLoader;

    public MappingMetaModelImpl(final TableBinder tableBinder, final DmlQueryBuilder dmlQueryBuilder, final JdbcTemplate jdbcTemplate, final ProxyFactory proxyFactory) {
        initialize(tableBinder, dmlQueryBuilder, jdbcTemplate);
        initEntityLoader(tableBinder, proxyFactory, dmlQueryBuilder, jdbcTemplate);
    }

    private void initialize(final TableBinder tableBinder, final DmlQueryBuilder dmlQueryBuilder, final JdbcTemplate jdbcTemplate) {
        EntityScanner.scan(packagePrefix).forEach(clazz -> {
            persistentClassMapping.putPersistentClass(clazz);

            final SingleTableEntityPersister entityPersister = new SingleTableEntityPersister(clazz.getName(), persistentClassMapping, tableBinder, dmlQueryBuilder, jdbcTemplate, persistentClassMapping.getPersistentClass(clazz));
            entityPersisterMap.put(entityPersister.getTargetEntityName(), entityPersister);
        });

        persistentClassMapping.setCollectionPersistentClassBinder();
    }

    private void initEntityLoader(final TableBinder tableBinder, final ProxyFactory proxyFactory, final DmlQueryBuilder dnmlQueryBuilder, final JdbcTemplate jdbcTemplate) {
        entityLoader = new SingleEntityLoader(tableBinder, persistentClassMapping, proxyFactory, dnmlQueryBuilder, jdbcTemplate);
    }

    @Override
    public EntityPersister getEntityDescriptor(final Object entity) {
        final String entityName = entity.getClass().getName();
        return entityPersisterMap.get(entityName);
    }

    @Override
    public EntityLoader getEntityLoader() {
        return this.entityLoader;
    }

    @Override
    public PersistentClassMapping getPersistentClassMapping() {
        return this.persistentClassMapping;
    }

}
