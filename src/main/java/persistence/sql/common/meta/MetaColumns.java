package persistence.sql.common.meta;

import java.util.HashMap;
import java.util.Map;

public class MetaColumns {

    private static final Map<Class<?>, Columns> map = new HashMap<>();

    public static Columns get(Class<?> clazz) {
        if (!map.containsKey(clazz)) {
            map.put(clazz, Columns.of(clazz.getDeclaredFields()));
        }

        return map.get(clazz);
    }
}
