package hibernate.event.persist;

import hibernate.action.EntityInsertAction;

public class SimplePersistEventListener implements PersistEventListener {

    // TODO void로 전체 변경 필요
    @Override
    public <T> Object onPersist(final PersistEvent<T> event) {
        event.getActionQueue()
                .addAction(new EntityInsertAction<>(event.getEntityPersister(), event.getEntity()));
        return null;
    }
}
