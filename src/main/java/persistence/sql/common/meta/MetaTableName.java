package persistence.sql.common.meta;

import java.util.HashMap;
import java.util.Map;

public class MetaTableName {

    private static final Map<Class<?>, TableName> map = new HashMap<>();

    public static TableName get(Class<?> clazz) {
        if (!map.containsKey(clazz)) {
            map.put(clazz, TableName.of(clazz));
        }

        return map.get(clazz);
    }
}
