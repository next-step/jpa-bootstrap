package database.sql.dml;

import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

public class SelectByPrimaryKey<T> {
    private final Select select;
    private Long id;

    public static <T> SelectByPrimaryKey<T> from(PersistentClass<T> persistentClass, Metadata metadata) {
        return new SelectByPrimaryKey<>(persistentClass, metadata);
    }

    private SelectByPrimaryKey(PersistentClass<T> persistentClass, Metadata metadata) {
        select = Select.from(persistentClass, metadata);
    }

    public SelectByPrimaryKey<T> byId(Long id) {
        this.id = id;
        return this;
    }

    public String buildQuery() {
        return select.id(this.id).buildQuery();
    }
}
