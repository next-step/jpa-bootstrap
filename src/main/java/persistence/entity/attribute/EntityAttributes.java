package persistence.entity.attribute;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class EntityAttributes {
    private static final Map<Class<?>, EntityAttribute> entityAttributeCenter = new HashMap<>();

    private static HashSet<Class<?>> createReferenceTracingLog() {
        return new HashSet<>();
    }

    public EntityAttribute findEntityAttribute(Class<?> clazz) {
        return entityAttributeCenter.get(clazz);
    }

    public void putEntityAttribute(Class<?> clazz) {
        entityAttributeCenter.put(clazz, EntityAttribute.of(clazz, createReferenceTracingLog()));
    }
}

