package event.listener.delete;

import builder.dml.EntityData;

public interface DeleteEventListener {
    void onDelete(EntityData entityData);
}
