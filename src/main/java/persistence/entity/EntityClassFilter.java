package persistence.entity;

import jakarta.persistence.Entity;
import java.util.Set;
import java.util.stream.Collectors;

public final class EntityClassFilter {
    public static Set<Class<?>> entityFilter(Set<Class<?>> classes) {
        return classes.stream()
                .filter(EntityClassFilter::isValidEntityClass)
                .collect(Collectors.toSet());
    }

    private static boolean isValidEntityClass(Class<?> clazz) {
        return clazz != null && clazz.isAnnotationPresent(Entity.class);
    }
}
