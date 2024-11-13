package builder.dml.builder;

import java.util.HashMap;
import java.util.Map;

public class DMLQueryBuilder {

    private final Map<BuilderName, Object> queryBuilder = new HashMap<>();

    public DMLQueryBuilder() {
        queryBuilder.put(BuilderName.SELECT_ALL, new SelectAllQueryBuilder());
        queryBuilder.put(BuilderName.SELECT_BY_ID, new SelectByIdQueryBuilder());
        queryBuilder.put(BuilderName.UPDATE, new UpdateQueryBuilder());
        queryBuilder.put(BuilderName.INSERT, new InsertQueryBuilder());
        queryBuilder.put(BuilderName.DELETE, new DeleteQueryBuilder());
    }

    public Object query(BuilderName name) {
        return queryBuilder.get(name);
    }

}
