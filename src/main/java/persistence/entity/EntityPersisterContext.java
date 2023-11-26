package persistence.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import jdbc.JdbcTemplate;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.OneToManyEntityPersister;
import persistence.entity.persister.OneToManyLazyEntityPersister;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.meta.EntityMeta;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;

public class EntityPersisterContext {
    private final Map<Class<?>, EntityPersister> context;

    private EntityPersisterContext(Map<Class<?>, EntityPersister> context) {
        this.context = context;

    }

    public static EntityPersisterContext create(MetaModel metaModel, JdbcTemplate jdbcTemplate,
                                                QueryGenerator queryGenerator) {
        final Map<Class<?>, EntityPersister> context = new ConcurrentHashMap<>();
        final Map<Class<?>, EntityMeta> entityMetaContext = metaModel.getEntityMetaContext();

        entityMetaContext.forEach((clazz, entityMeta) -> {
            EntityPersister entityPersister = genrateEntityPersister(jdbcTemplate, queryGenerator, entityMeta);
            context.put(clazz, entityPersister);
        });

        return new EntityPersisterContext(context);
    }

    private static EntityPersister genrateEntityPersister(JdbcTemplate jdbcTemplate, QueryGenerator queryGenerator,
                                                          EntityMeta entityMeta) {
        if (entityMeta.hasLazyOneToMayAssociation()) {
            return OneToManyLazyEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
        }
        if (entityMeta.hasOneToManyAssociation()) {
            return OneToManyEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
        }

        return SimpleEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
    }

    public EntityPersister getEntityPersister(Class<?> tClass) {
        return context.get(tClass);
    }

}
