package persistence.event.dirtycheck;

import persistence.event.EventListener;

public interface DirtyCheckEventListener extends EventListener {
    <T> void onDirtyCheck(DirtyCheckEvent<T> dirtyCheckEvent);
}
