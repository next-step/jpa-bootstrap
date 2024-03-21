package persistence.entity.event.listener;

import persistence.sql.meta.MetaModel;

public abstract class AbstractEntityListener {

    protected MetaModel metaModel;

    AbstractEntityListener(MetaModel metaModel) {
        this.metaModel = metaModel;
    }
}
