package hibernate.event.load;

public class SimpleLoadEventListener implements LoadEventListener {

    @Override
    public <T> T onLoad(LoadEvent<T> event) {
        return event.getEntityLoader()
                .find(event.getEntityId());
    }
}
