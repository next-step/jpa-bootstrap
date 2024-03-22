package persistence.entity.event;

import java.util.Map;
import persistence.sql.meta.Column;

public class RelationEntityEvent<T> extends EntityEvent<T> {

    private Map<Column, Object> where;

    public RelationEntityEvent(Class<T> entityClass, EventType eventType, Map<Column, Object> where) {
        super(entityClass, eventType);
        this.where = where;
    }

    public Map<Column, Object> getWhere() {
        return where;
    }
}
