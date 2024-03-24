package persistence.entity.context;

import java.util.Objects;

public class EntityKey {
    private final String className;
    private final Long id;

    public static EntityKey of(Class<?> clazz, Long id) {
        if (id == null) {
            throw new RuntimeException("id is null");
        }
        return new EntityKey(clazz.getName(), id);
    }

    private EntityKey(String className, Long id) {
        this.className = className;
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(className, entityKey.className) && Objects.equals(id, entityKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, id);
    }
}
