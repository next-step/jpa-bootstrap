package persistence.event;

public interface DirtyCheckEventListener {
    <T> void onDirtyCheck(DirtyCheckEvent<T> dirtyCheckEvent);
}
