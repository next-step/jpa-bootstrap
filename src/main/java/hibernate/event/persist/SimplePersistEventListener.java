package hibernate.event.persist;

public class SimplePersistEventListener implements PersistEventListener {

    @Override
    public <T> Object onPersist(PersistEvent<T> event) {
        return event.getEntityPersister()
                .insert(event.getEntity());
    }
}
