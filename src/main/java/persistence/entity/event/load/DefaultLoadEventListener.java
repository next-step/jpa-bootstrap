package persistence.entity.event.load;

import bootstrap.MetaModel;
import persistence.entity.EntityLoader;

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

}
