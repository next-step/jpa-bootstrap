package persistence.entity;

public interface EntityLoader {

    <T, ID> T find(Class<T> clazz, ID id);
}
