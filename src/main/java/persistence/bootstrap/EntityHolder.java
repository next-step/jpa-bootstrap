package persistence.bootstrap;

import java.util.Arrays;
import java.util.List;

public class EntityHolder {
    private final List<Class<?>> entityTypes;

    public EntityHolder(String... basePackages) {
        this.entityTypes = Arrays.stream(basePackages)
                .flatMap(basePackage -> EntityScanner.scan(basePackage).stream())
                .toList();
    }

    public List<Class<?>> getEntityTypes() {
        return entityTypes;
    }
}
