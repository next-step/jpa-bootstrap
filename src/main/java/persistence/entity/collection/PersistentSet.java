package persistence.entity.collection;

import java.util.HashSet;
import java.util.Set;

public class PersistentSet<T> extends AbstractPersistentCollection<T> implements Set<T> {

    public PersistentSet() {
        super(new HashSet<>());
    }
}
