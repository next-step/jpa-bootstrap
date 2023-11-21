package hibernate.event.load;

import hibernate.metamodel.MetaModel;

public class SimpleLoadEventListener implements LoadEventListener {

    private final MetaModel metaModel;

    public SimpleLoadEventListener(final MetaModel metaModel) {
        this.metaModel = metaModel;
    }

    @Override
    public <T> T onLoad(final LoadEvent<T> event) {
        return metaModel.getEntityLoader(event.getClazz())
                .find(event.getEntityId());
    }
}
