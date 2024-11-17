package persistence.bootstrap.binder;

import jakarta.persistence.Entity;
import persistence.bootstrap.ComponentScanner;

import java.util.List;

public class EntityBinder {
    private final List<Class<?>> entityTypes;

    public EntityBinder(String... basePackages) {
        final List<Class<?>> classes = ComponentScanner.scan(basePackages);
        this.entityTypes = findEntity(classes);
    }

    public List<Class<?>> getEntityTypes() {
        return entityTypes;
    }

    private List<Class<?>> findEntity(List<Class<?>> classes) {
        return classes.stream()
                .filter(this::isEntity)
                .toList();
    }

    private boolean isEntity(Class<?> clazz) {
        return clazz.isAnnotationPresent(Entity.class);
    }
}
