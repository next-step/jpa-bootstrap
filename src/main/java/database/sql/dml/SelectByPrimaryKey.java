package database.sql.dml;

import persistence.entity.context.PersistentClass;

import java.util.List;

public class SelectByPrimaryKey<T> {
    private final Select select;
    private Long id;

    public static <T> SelectByPrimaryKey<T> from(PersistentClass<T> persistentClass, List<Class<?>> entities) {
        return new SelectByPrimaryKey<>(persistentClass, entities);
    }

    private SelectByPrimaryKey(PersistentClass<T> persistentClass, List<Class<?>> entities) {
        select = Select.from(persistentClass, entities);
    }

    public SelectByPrimaryKey<T> byId(Long id) {
        this.id = id;
        return this;
    }

    public String buildQuery() {
        return select.id(this.id).buildQuery();
    }
}
