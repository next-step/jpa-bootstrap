package persistence.event.flush;

public interface FlushEventListener {
    void onFlush(FlushEvent flushEvent);
}
