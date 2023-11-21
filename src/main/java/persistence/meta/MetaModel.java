package persistence.meta;

import java.util.Map;
import persistence.sql.QueryGenerator;

public class MetaModel {
    private final Map<Class<?>, EntityMeta> entityMetaContext;
    private final Map<Class<?>, QueryGenerator> queryGeneratorContext;

    public MetaModel(Map<Class<?>, EntityMeta> entityMetaContext, Map<Class<?>, QueryGenerator> queryGeneratorContext) {
        this.entityMetaContext = entityMetaContext;
        this.queryGeneratorContext = queryGeneratorContext;
    }

    public Map<Class<?>, EntityMeta> getEntityMetaContext() {
        return entityMetaContext;
    }

    public Map<Class<?>, QueryGenerator> getQueryGeneratorContext() {
        return queryGeneratorContext;
    }
}
