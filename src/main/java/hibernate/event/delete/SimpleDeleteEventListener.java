package hibernate.event.delete;

import hibernate.action.EntityDeleteAction;
import hibernate.metamodel.MetaModel;

public class SimpleDeleteEventListener implements DeleteEventListener {

    private final MetaModel metaModel;

    public SimpleDeleteEventListener(final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public <T> void onDelete(final DeleteEvent<T> event) {
        event.getActionQueue()
                .addAction(new EntityDeleteAction<>(metaModel.getEntityPersister(event.getClazz()), event.getEntity()));
    }
}
