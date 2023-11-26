package persistence.meta;

import java.util.Map;

public class MetaModel {
    private final Map<Class<?>, EntityMeta> entityMetaContext;

    public MetaModel(Map<Class<?>, EntityMeta> entityMetaContext) {
        this.entityMetaContext = entityMetaContext;
    }

    public Map<Class<?>, EntityMeta> getEntityMetaContext() {
        return entityMetaContext;
    }
}
