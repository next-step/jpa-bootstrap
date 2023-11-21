package hibernate.event.merge;

import hibernate.action.EntityUpdateAction;
import hibernate.metamodel.MetaModel;

public class SimpleMergeEventListener implements MergeEventListener {

    private final MetaModel metaModel;

    public SimpleMergeEventListener(final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public <T> void onMerge(final MergeEvent<T> event) {
        event.getActionQueue()
                .addAction(new EntityUpdateAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntityId(), event.getChangeColumns()));
    }
}
