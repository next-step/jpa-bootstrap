package persistence.sql.meta;

import jakarta.persistence.Entity;

public class EntityScanFilter implements MetaScanFilterStrategy {

    @Override
    public boolean match(Class<?> clazz) {
        return clazz.isAnnotationPresent(Entity.class);
    }
}
