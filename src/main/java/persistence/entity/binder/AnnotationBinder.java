package persistence.entity.binder;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import persistence.dialect.Dialect;
import persistence.meta.EntityMeta;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;

public final class AnnotationBinder {

    public static MetaModel bindMetaModel(Set<Class<?>> classes, Dialect dialect) {
        if (classes == null || classes.isEmpty()) {
            throw new IllegalArgumentException("bind할 클래스가 없습니다.");
        }

        final Map<Class<?>, EntityMeta> entityMetaMap = bindEntityMetaMap(classes);
        return new MetaModel(entityMetaMap, bindQueryGeneratorMap(entityMetaMap, dialect));
    }

    private static Map<Class<?>, EntityMeta> bindEntityMetaMap(Set<Class<?>> classes) {
        Map<Class<?>, EntityMeta> entityMetaMap = new ConcurrentHashMap<>();
        for (Class<?> clazz : classes) {
            entityMetaMap.put(clazz, EntityMeta.from(clazz));
        }
        return entityMetaMap;
    }

    private static Map<Class<?>, QueryGenerator> bindQueryGeneratorMap(Map<Class<?>, EntityMeta> entityMetaMap,
                                                                       Dialect dialect) {
        Map<Class<?>, QueryGenerator> queryGeneratorMap = new ConcurrentHashMap<>();
        entityMetaMap.forEach((clazz, entityMeta) -> {
            QueryGenerator queryGenerator = QueryGenerator.of(entityMeta, dialect);
            queryGeneratorMap.put(clazz, queryGenerator);
        });
        return queryGeneratorMap;
    }
}
