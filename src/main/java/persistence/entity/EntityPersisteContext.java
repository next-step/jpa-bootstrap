package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jdbc.JdbcTemplate;
import persistence.entity.binder.EntityPersisterBinder;
import persistence.entity.persister.EntityPersister;
import persistence.meta.EntityMeta;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;

public class EntityPersisteContext {
    private final Map<Class<?>, EntityPersister> context;

    private EntityPersisteContext(Map<Class<?>, EntityPersister> context) {
        this.context = context;
    }

    public static EntityPersisteContext create(MetaModel metaModel, JdbcTemplate jdbcTemplate) {
        Map<Class<?>, EntityPersister> context = new ConcurrentHashMap<>();
        final Map<Class<?>, EntityMeta> metaMap = metaModel.getMetaMap();
        final Map<Class<?>, QueryGenerator> queryGeneratorMap = metaModel.getQueryGeneratorMap();

        metaMap.forEach((clazz, entityMeta) -> {
            QueryGenerator queryGenerator = queryGeneratorMap.get(clazz);
            EntityPersister entityPersister = EntityPersisterBinder.bind(jdbcTemplate, queryGenerator, entityMeta);
            context.put(clazz, entityPersister);
        });

        return new EntityPersisteContext(context);
    }

    public EntityPersister getEntityPersister(Class<?> tClass) {
        return context.get(tClass);
    }

}
