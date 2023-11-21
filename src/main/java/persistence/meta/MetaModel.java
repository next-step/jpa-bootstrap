package persistence.meta;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import persistence.entity.EntityPersisteContext;
import persistence.sql.QueryGenerator;

public class MetaModel {
    private final Map<Class<?>, EntityMeta> metaMap;
    private final Map<Class<?>, QueryGenerator> queryGeneratorMap;

    public MetaModel(Map<Class<?>, EntityMeta> metaMap, Map<Class<?>, QueryGenerator> queryGeneratorMap) {
        this.metaMap = metaMap;
        this.queryGeneratorMap = queryGeneratorMap;
    }
    public Map<Class<?>, EntityMeta> getMetaMap() {
        return metaMap;
    }
    public Map<Class<?>, QueryGenerator> getQueryGeneratorMap() {
        return queryGeneratorMap;
    }
}
