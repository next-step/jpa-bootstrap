package persistence.meta;

import java.util.Map;
import persistence.dialect.Dialect;
import persistence.sql.QueryGenerator;

public class MetaModel {
    private final Map<Class<?>, EntityMeta> entityMetaContext;
    private final QueryGenerator queryGenerator;

    public MetaModel(Map<Class<?>, EntityMeta> entityMetaContext, Dialect dialect) {
        this.entityMetaContext = entityMetaContext;
        this.queryGenerator = QueryGenerator.of(dialect);
    }

    public Map<Class<?>, EntityMeta> getEntityMetaContext() {
        return entityMetaContext;
    }

    public QueryGenerator getQueryGenerator() {
        return queryGenerator;
    }
}
