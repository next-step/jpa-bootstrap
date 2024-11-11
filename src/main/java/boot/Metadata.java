package boot;

import persistence.sql.dml.Database;
import persistence.sql.dml.MetadataLoader;
import persistence.sql.dml.impl.SimpleMetadataLoader;
import persistence.sql.node.EntityNode;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Metadata {
    private final Map<Class<?>, MetadataLoader<?>> entityBindingMap;
    private final Database database;

    public Metadata(Map<Class<?>, MetadataLoader<?>> entityBindingMap, Database database) {
        this.entityBindingMap = entityBindingMap;
        this.database = database;
    }

    public static Metadata create(Set<EntityNode<?>> entities, Database database) {
        Map<Class<?>, MetadataLoader<?>> bindingMap = entities.stream()
                .map(node -> new SimpleMetadataLoader<>(node.entityClass()))
                .collect(Collectors.toMap(MetadataLoader::getEntityType, Function.identity()));

        return new Metadata(bindingMap, database);
    }

    public Database database() {
        return database;
    }

    public MetadataLoader<?> metadataLoader(Class<?> entityType) {
        if (entityType == null || !entityBindingMap.containsKey(entityType)) {
            throw new IllegalArgumentException("Not Found MetadataLoader | from: " + entityType);
        }

        return entityBindingMap.get(entityType);
    }

    public Map<Class<?>, MetadataLoader<?>> getEntityBinding() {
        return new HashMap<>(entityBindingMap);
    }
}
