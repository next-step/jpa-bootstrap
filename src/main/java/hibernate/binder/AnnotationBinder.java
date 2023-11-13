package hibernate.binder;

import jakarta.persistence.Entity;

import java.util.List;
import java.util.stream.Collectors;

public class AnnotationBinder {

    private static final Class<Entity> ENTITY_ANNOTATION = Entity.class;

    private AnnotationBinder() {
    }

    public static List<Class<?>> parseEntityClasses(String basePackage) throws ClassNotFoundException {
        List<Class<?>> classes = ComponentScanner.scan(basePackage);
        return classes.stream()
                .filter(clazz -> clazz.isAnnotationPresent(ENTITY_ANNOTATION))
                .collect(Collectors.toList());
    }
}
