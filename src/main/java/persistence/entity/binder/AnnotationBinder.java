package persistence.entity.binder;

import jakarta.persistence.Entity;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import persistence.dialect.Dialect;
import persistence.entity.ClassScanner;
import persistence.meta.EntityMeta;
import persistence.meta.MetaModel;

public final class AnnotationBinder {

    public static MetaModel bindMetaModel(String pakageName, Dialect dialect) {
        final Set<Class<?>> classes = entityFilter(ClassScanner.scan(pakageName));

        if (classes == null || classes.isEmpty()) {
            throw new IllegalArgumentException("bind할 클래스가 없습니다.");
        }

        final Map<Class<?>, EntityMeta> entityMetaMap = bindEntityMetaMap(classes);
        return new MetaModel(entityMetaMap, dialect);
    }

    private static Map<Class<?>, EntityMeta> bindEntityMetaMap(Set<Class<?>> classes) {
        Map<Class<?>, EntityMeta> entityMetaMap = new ConcurrentHashMap<>();
        for (Class<?> clazz : classes) {
            entityMetaMap.put(clazz, EntityMeta.from(clazz));
        }
        return entityMetaMap;
    }

    private static Set<Class<?>> entityFilter(Set<Class<?>> classes) {
        return classes.stream()
                .filter(AnnotationBinder::isValidEntityClass)
                .collect(Collectors.toSet());
    }

    private static boolean isValidEntityClass(Class<?> clazz) {
        return clazz != null && clazz.isAnnotationPresent(Entity.class);
    }

}
