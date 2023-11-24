package persistence.meta;

import java.util.Map;
import persistence.dialect.Dialect;

public class MetaModel {
    private final Map<Class<?>, EntityMeta> entityMetaContext;
    private final Dialect dialect;

    public MetaModel(Map<Class<?>, EntityMeta> entityMetaContext, Dialect dialect) {
        this.entityMetaContext = entityMetaContext;
        this.dialect = dialect;
    }

    public Map<Class<?>, EntityMeta> getEntityMetaContext() {
        return entityMetaContext;
    }

    public Dialect getDialect() {
        return dialect;
    }
}
