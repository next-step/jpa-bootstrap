package boot;

import jakarta.persistence.Entity;
import persistence.entity.EntityMeta;

import java.util.Map;
import java.util.stream.Collectors;

public class EntityBinder {

    private EntityBinder() {
        throw new IllegalStateException("Utility Class cannot be instantiated");
    }

    public static Map<Class<?>, EntityMeta> bind(String basePackage) {
        try {
            ComponentScanner scanner = new ComponentScanner();
            return scanner.scan(basePackage).stream()
                    .filter(it -> it.isAnnotationPresent(Entity.class))
                    .collect(Collectors.toMap(clazz -> clazz, EntityMeta::from));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to Scan MetaModels", e);
        }
    }

}
