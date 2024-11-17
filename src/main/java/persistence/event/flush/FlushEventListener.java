package persistence.event.flush;

import persistence.event.EventListener;

public interface FlushEventListener extends EventListener {
    void onFlush(FlushEvent flushEvent);
}
