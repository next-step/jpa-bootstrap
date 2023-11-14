package persistence.sql.meta;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MetaFactory {

    private static final Map<String, EntityMeta> metaMap = new ConcurrentHashMap<>();

    private MetaFactory() {}

    private static void put(Class<?> clazz) {
        metaMap.put(clazz.getName(), EntityMeta.of(clazz));
    }

    public static EntityMeta get(Class<?> clazz) {
        if (metaMap.containsKey(clazz.getName())) {
            return metaMap.get(clazz.getName());
        }
        put(clazz);
        return metaMap.get(clazz.getName());
    }

}
