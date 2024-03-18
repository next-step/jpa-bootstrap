package persistence.entity;

import persistence.sql.meta.IdColumn;
import persistence.sql.meta.Table;
import utils.ValueExtractor;
import utils.ValueInjector;

public class EntityMeta<T> {
    private final Table table;
    private final IdColumn idColumn;

    public EntityMeta(Table table, IdColumn idColumn) {
        this.table = table;
        this.idColumn = idColumn;
    }

    public static <T> EntityMeta<T> from(T entity) {
        Table table = Table.from(entity.getClass());
        IdColumn idColumn = table.getIdColumn();
        return new EntityMeta<>(table, idColumn);
    }

    public static <T> EntityMeta<T> from(Class<?> clazz) {
        Table table = Table.from(clazz);
        IdColumn id = table.getIdColumn();
        return new EntityMeta<>(table, id);
    }

    public boolean isNew(Object entity) {
        return getId(entity) == null;
    }

    public Object getId(Object entity) {
        return ValueExtractor.extract(entity, idColumn);
    }

    public void injectId(Object entity, Object generatedId) {
        ValueInjector.inject(entity, idColumn, generatedId);
    }

    public Table getTable() {
        return table;
    }

    public IdColumn getIdColumn() {
        return idColumn;
    }

    public Class<T> getClazz() {
        return (Class<T>) table.getClazz();
    }
}
