package persistence.entity.binder;

import jdbc.JdbcTemplate;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.OneToManyEntityPersister;
import persistence.entity.persister.OneToManyLazyEntityPersister;
import persistence.entity.persister.SimpleEntityPersister;
import persistence.meta.EntityMeta;
import persistence.sql.QueryGenerator;

public class EntityPersisterBinder {

    public static EntityPersister bind(JdbcTemplate jdbcTemplate, QueryGenerator queryGenerator,
                                                EntityMeta entityMeta) {
        if (entityMeta.hasLazyOneToMayAssociation()) {
            return OneToManyLazyEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
        }
        if (entityMeta.hasOneToManyAssociation()) {
            return OneToManyEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
        }

        return SimpleEntityPersister.create(jdbcTemplate, queryGenerator, entityMeta);
    }
}
