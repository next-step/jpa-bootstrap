package database.mapping;

import database.dialect.Dialect;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import persistence.bootstrap.Metadata;
import persistence.entity.context.PersistentClass;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public class Association {

    private final Field field;

    public Association(Field field) {
        this.field = field;
    }

    public static Association fromField(Field field) {
        return new Association(field);
    }

    public String getForeignKeyColumnName() {
        JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        return joinColumn.name();
    }

    public String getFieldName() {
        return field.getName();
    }

    public Class<?> getFieldType() {
        return field.getType();
    }

    public Class<?> getFieldGenericType() {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return (Class<?>) genericType.getActualTypeArguments()[0];
    }

    public PersistentClass<?> getGenericTypeClass(Metadata metadata) {
        return metadata.getPersistentClass(this.getFieldGenericType());
    }

    public String getTableName(Metadata metadata) {
        return getGenericTypeClass(metadata).getTableName();
    }

    public String getForeignKeyColumnType(Metadata metadata, Dialect dialect) {
        Class<?> foreignKeyColumnType =
                (Class<?>) getGenericTypeClass(metadata)
                        .getPrimaryKey()
                        .getFieldType();

        return dialect.convertToSqlTypeDefinition(foreignKeyColumnType, 0);
    }

    public boolean isLazyLoad() {
        OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        return oneToMany.fetch() == FetchType.LAZY;
    }
}
