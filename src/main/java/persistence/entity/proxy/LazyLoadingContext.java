package persistence.entity.proxy;

import java.util.Map;
import java.util.function.Consumer;
import persistence.entity.event.EventType;
import persistence.entity.event.RelationEntityEvent;
import persistence.entity.event.listener.EntityEventDispatcher;
import persistence.sql.meta.Column;
import persistence.sql.meta.RelationTable;
import persistence.sql.meta.Table;

public class LazyLoadingContext {
    private final Table root;
    private final Table relationTable;
    private final Column joinColumn;
    private final Object instance;
    private final EntityEventDispatcher dispatcher;
    private final Consumer<Object> consumer;

    public LazyLoadingContext(Table root, Table relationTable, Object instance, EntityEventDispatcher dispatcher, Consumer<Object> consumer) {
        this.root = root;
        this.relationTable = relationTable;
        this.joinColumn = RelationTable.getJoinColumn(root, relationTable);
        this.instance = instance;
        this.dispatcher = dispatcher;
        this.consumer = consumer;
    }

    public Class<?> getLazyLoadClass() {
        return joinColumn.getType();
    }

    public Object loading() {
        Object entity = dispatcher.dispatch(new RelationEntityEvent<>(relationTable.getClazz(), EventType.LOAD_RELATION,
            Map.of(joinColumn, root.getIdValue(instance))));
        consumer.accept(entity);
        return entity;
    }
}
