package persistence.sql.common.meta;

import java.util.HashMap;
import java.util.Map;

public class MetaJoinColumn {

    private static final Map<Class<?>, JoinColumn> map = new HashMap<>();

    public static JoinColumn get(Class<?> clazz) {
        if (!map.containsKey(clazz)) {
            map.put(clazz, JoinColumn.of(clazz.getDeclaredFields()));
        }

        return map.get(clazz);
    }
}
