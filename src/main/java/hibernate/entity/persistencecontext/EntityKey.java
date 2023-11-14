package hibernate.entity.persistencecontext;

import java.util.Objects;

public class EntityKey {

    private final Object id;
    private final Class<?> clazz;

    public EntityKey(final Object id, final Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public EntityKey(final Object id, final Object object) {
        this(id, object.getClass());
    }

    @Override
    public boolean equals(final Object entity) {
        if (this == entity) return true;
        if (entity == null || getClass() != entity.getClass()) return false;
        EntityKey entityKey = (EntityKey) entity;
        return Objects.equals(id, entityKey.id) && Objects.equals(clazz, entityKey.clazz);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clazz);
    }
}
