package persistence.entity.context;

import java.util.Objects;

public class EntityKey {
    private final String entityClass;
    private final Long id;

    private EntityKey(String entityClass, Long id) {
        this.entityClass = entityClass;
        this.id = id;
    }

    public static EntityKey of(Class<?> clazz, Long id) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }
        return new EntityKey(clazz.getName(), id);
    }

    public static EntityKey of(String className, Long id) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }
        return new EntityKey(className, id);
    }

    public static EntityKey of(Object entity) {
        Class<?> clazz = entity.getClass();
        PersistentClass<?> persistentClass = PersistentClass.from(clazz);
        Long id = persistentClass.getPrimaryKeyValue(entity);

        return EntityKey.of(clazz, id);
    }

    public static <T> EntityKey of(PersistentClass<T> persistentClass, Long id) {
        return EntityKey.of(persistentClass.getMappedClassName(), id);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(entityClass, entityKey.entityClass) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(entityClass, id);
    }
}
