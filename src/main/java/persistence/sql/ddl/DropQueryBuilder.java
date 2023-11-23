package persistence.sql.ddl;

import jakarta.persistence.Entity;
import persistence.dialect.Dialect;
import persistence.exception.NoEntityException;
import persistence.meta.TableName;
import persistence.sql.QueryBuilder;

public class DropQueryBuilder extends QueryBuilder {

    public DropQueryBuilder(Dialect dialect) {
        super(dialect);
    }

    public String build(Class<?> clazz) {
        if (clazz == null || clazz.getAnnotation(Entity.class) == null) {
            throw new NoEntityException();
        }
        return dialect.dropTable(TableName.from(clazz).getValue());
    }
}
