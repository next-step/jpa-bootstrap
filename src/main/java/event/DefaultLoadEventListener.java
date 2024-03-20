package event;

import boot.metamodel.MetaModel;

public class DefaultLoadEventListener implements LoadEventListener {

    private final MetaModel metaModel;

    public DefaultLoadEventListener(MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public <T> T onLoad(LoadEvent<T> loadEvent) {
        return metaModel.getEntityLoader(loadEvent.getClazz())
                .find(loadEvent.getId());
    }
}
