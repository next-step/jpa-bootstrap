package persistence.entity.event.load;

public class LoadEvent <ID> {

    private final ID id;
    private final Class<?> clazz;

    public LoadEvent(ID id, Class<?> clazz) {
        this.id = id;
        this.clazz = clazz;
    }

    public ID getId() {
        return id;
    }

    public Class<?> getClazz() {
        return clazz;
    }
}
