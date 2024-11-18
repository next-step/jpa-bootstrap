package persistence.event.dirtycheck;

public interface DirtyCheckEventListener {
    <T> void onDirtyCheck(DirtyCheckEvent<T> dirtyCheckEvent);
}
