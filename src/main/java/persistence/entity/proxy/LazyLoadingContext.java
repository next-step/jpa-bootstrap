package persistence.entity.proxy;

import java.util.Map;
import java.util.function.Consumer;
import persistence.entity.loader.EntityLoader;
import persistence.sql.meta.Column;
import persistence.sql.meta.RelationTable;
import persistence.sql.meta.Table;

public class LazyLoadingContext {
    private final Table root;
    private final Column joinColumn;
    private final Table relationTable;
    private final Object instance;
    private final EntityLoader entityLoader;
    private final Consumer<Object> consumer;

    public LazyLoadingContext(Table root, Table relationTable, Object instance, EntityLoader entityLoader, Consumer<Object> consumer) {
        this.root = root;
        this.relationTable = relationTable;
        this.joinColumn = RelationTable.getJoinColumn(root, relationTable);
        this.instance = instance;
        this.entityLoader = entityLoader;
        this.consumer = consumer;
    }

    public Class<?> getLazyLoadClass() {
        return joinColumn.getType();
    }

    public Object loading() {
        Object entity = entityLoader.find(relationTable.getClazz(), Map.of(joinColumn, root.getIdValue(instance)));
        consumer.accept(entity);
        return entity;
    }
}
