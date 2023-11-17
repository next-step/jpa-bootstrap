package hibernate.event.delete;

public class SimpleDeleteEventListener implements DeleteEventListener {

    @Override
    public <T> void onDelete(DeleteEvent<T> event) {
        event.getEntityPersister()
                .delete(event.getEntity());
    }
}
