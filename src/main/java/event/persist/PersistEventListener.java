package event.persist;

import builder.dml.EntityData;

public interface PersistEventListener {

    void onPersist(EntityData entityData);

}
