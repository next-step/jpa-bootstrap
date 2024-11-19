package event.listener.merge;

import builder.dml.EntityData;

public interface MergeEventListener {
    void onMerge(EntityData entityData);
}
