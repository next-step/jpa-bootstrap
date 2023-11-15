package persistence.sql.meta;

import java.util.HashMap;
import java.util.Map;

public class MetaFactory {

    private static MetaFactory INSTANCE;

    private final Map<String, EntityMeta> metaMap = new HashMap<>();

    private MetaFactory() {
        EntityMetaScanner metaScanner = new EntityMetaScanner(new EntityScanFilter());
        metaScanner.scan().forEach(entityMeta -> {
            Class<?> innerClass = entityMeta.getInnerClass();
            metaMap.put(innerClass.getName(), entityMeta);
        });
    }

    public static MetaFactory getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MetaFactory();
        }
        return INSTANCE;
    }

    public EntityMeta get(Class<?> clazz) {
        return metaMap.get(clazz.getName());
    }

}
