package persistence.sql.meta;

@FunctionalInterface
public interface MetaScanFilterStrategy {

    boolean match(Class<?> clazz);
}
