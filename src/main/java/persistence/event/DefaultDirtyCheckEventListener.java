package persistence.event;

import persistence.bootstrap.Metamodel;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.Objects;

public class DefaultDirtyCheckEventListener implements DirtyCheckEventListener {
    @Override
    public <T> void onDirtyCheck(DirtyCheckEvent<T> dirtyCheckEvent) {
        final Metamodel metamodel = dirtyCheckEvent.getMetamodel();
        final T entity = dirtyCheckEvent.getEntity();
        final T snapshot = dirtyCheckEvent.getSnapshot();

        final EntityTable entityTable = metamodel.getEntityTable(entity.getClass());
        final List<EntityColumn> result = entityTable.getEntityColumns()
                .stream()
                .filter(entityColumn -> isDirtied(entity, snapshot, entityColumn))
                .toList();
        dirtyCheckEvent.setResult(result);
    }

    private boolean isDirtied(Object entity, Object snapshot, EntityColumn entityColumn) {
        return !Objects.equals(entityColumn.extractValue(entity), entityColumn.extractValue(snapshot));
    }
}
