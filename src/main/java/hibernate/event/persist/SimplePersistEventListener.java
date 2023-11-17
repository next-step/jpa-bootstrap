package hibernate.event.persist;

public class SimplePersistEventListener implements PersistEventListener {

    @Override
    public <T> void onPersist(PersistEvent<T> event) {
        event.getEntityPersister()
                .insert(event.getEntity());
    }
}
