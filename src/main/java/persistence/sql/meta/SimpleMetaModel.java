package persistence.sql.meta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jdbc.JdbcTemplate;
import persistence.entity.loader.EntityLoader;
import persistence.entity.loader.SimpleEntityLoader;
import persistence.entity.persister.EntityPersister;
import persistence.entity.persister.SimpleEntityPersister;

public class SimpleMetaModel implements MetaModel {

    private final Map<Class<?>, EntityPersister<?>> entityPersisterMap = new HashMap<>();
    private final Map<Class<?>, EntityLoader<?>> entityLoaderMap = new HashMap<>();


    private SimpleMetaModel() {
    }

    public static SimpleMetaModel of(JdbcTemplate jdbcTemplate, List<Class<?>> classes) {

        if (classes.isEmpty()) {
            throw new IllegalArgumentException("basePackage에 Entity 클래스가 존재하지 않습니다.");
        }

        SimpleMetaModel metaModel = new SimpleMetaModel();
        classes.forEach(clazz -> {
            metaModel.entityPersisterMap.put(clazz, SimpleEntityPersister.of(jdbcTemplate, clazz));
            metaModel.entityLoaderMap.put(clazz, SimpleEntityLoader.of(jdbcTemplate, clazz));
        });

        return metaModel;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> EntityPersister<T> getEntityPersister(Class<T> t) {
        return (EntityPersister<T>) entityPersisterMap.get(t);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> EntityLoader<T> getEntityLoader(Class<T> t) {
        return (EntityLoader<T>) entityLoaderMap.get(t);
    }
}
