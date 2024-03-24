package persistence.entity.event.load;

import bootstrap.MetaModel;
import persistence.entity.EntityLoader;
import persistence.entity.event.PersistEvent;

public class DefaultLoadEventListener implements LoadEventListener {

    private final MetaModel metaModel;

    public DefaultLoadEventListener(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public <ID, T> T onLoad(LoadEvent<ID> event) {
        EntityLoader entityLoader = metaModel.getEntityLoader(event.getClazz());
        return (T) entityLoader.find(event.getClazz(), event.getId());
    }

    @Override
    public <T, ID> void fireEvent(PersistEvent<T, ID> event) {
        throw new UnsupportedOperationException();
    }
}
