package persistence.event.flush;

public class DefaultFlushEventListener implements FlushEventListener {

    @Override
    public void onFlush(FlushEvent event) {
        event.getSession().getActionQueue().executeAll();
    }
}
