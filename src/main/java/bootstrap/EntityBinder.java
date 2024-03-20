package bootstrap;

import jakarta.persistence.Entity;

import java.util.List;
import java.util.stream.Collectors;

public class EntityBinder implements Binder {
    @Override
    public List<Class<?>> bind(String basePackage) {
        return ComponentScanner.scan(basePackage).stream()
                .filter(clazz -> clazz.isAnnotationPresent(Entity.class))
                .collect(Collectors.toList());
    }
}
