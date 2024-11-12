package builder.dml;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.lang.reflect.Field;
import java.util.Arrays;

public class EntityMetaData {

    private static final String NOT_EXIST_ENTITY_ANNOTATION = "@Entity 어노테이션이 존재하지 않습니다.";

    private final Class<?> clazz;
    private final String tableName;
    private final String pkName;
    private final String alias;

    public <T> EntityMetaData(Class<T> clazz) {
        confirmEntityAnnotation(clazz);
        this.clazz = clazz;
        this.tableName = getTableName(clazz);
        this.pkName = getPkName(clazz);
        this.alias = QueryBuildUtil.getAlias(this.tableName);
    }

    public String getTableName() {
        return this.tableName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public String getPkName() {
        return pkName;
    }

    public String getAlias() {
        return alias;
    }

    private String getPkName(Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Id.class))
                .map(Field::getName)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Entity에 PK가 존재하지 않습니다."));
    }

    private void confirmEntityAnnotation(Class<?> entityClass) {
        if (!entityClass.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(NOT_EXIST_ENTITY_ANNOTATION);
        }
    }

    private String getTableName(Class<?> entityClass) {
        if (entityClass.isAnnotationPresent(Table.class)) {
            Table table = entityClass.getAnnotation(Table.class);
            return table.name();
        }
        return entityClass.getSimpleName();
    }
}

