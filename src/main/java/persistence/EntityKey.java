package persistence;

import builder.dml.EntityData;

import java.util.Objects;

public class EntityKey {

    private final Object id;
    private final Class<?> clazz;

    public EntityKey(Object id, Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public EntityKey(EntityData entityData) {
        this.id = entityData.getId();
        this.clazz = entityData.getClazz();
    }

    public Object getId() {
        return id;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        EntityKey entityKey = (EntityKey) object;
        return Objects.equals(id, entityKey.id) && this.clazz == entityKey.getClazz();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, clazz);
    }
}
