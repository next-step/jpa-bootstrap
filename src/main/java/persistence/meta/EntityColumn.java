package persistence.meta;

import jakarta.persistence.FetchType;

import java.lang.reflect.Field;
import java.util.Objects;

public class EntityColumn {
    private final Field field;
    private final Class<?> type;
    private final ColumnName columnName;
    private final ColumnLength columnLength;
    private final ColumnIdOption columnIdOption;
    private final ColumnOption columnOption;
    private ColumnValue columnValue;

    public EntityColumn(Field field) {
        this.field = field;
        this.type = field.getType();
        this.columnName = new ColumnName(field);
        this.columnLength = new ColumnLength(field);
        this.columnIdOption = new ColumnIdOption(field);
        this.columnOption = new ColumnOption(field);
    }

    public Field getField() {
        return field;
    }

    public Class<?> getType() {
        return type;
    }

    public String getColumnName() {
        return columnName.value();
    }

    public int getColumnLength() {
        return columnLength.value();
    }

    public boolean isId() {
        return columnIdOption.isId();
    }

    public boolean isGenerationValue() {
        return columnIdOption.isGenerationValue();
    }

    public boolean isNotNull() {
        return columnOption.isNotNull();
    }

    public Object getValue() {
        return columnValue.value();
    }

    public void setValue(Field field, Object entity) {
        columnValue = new ColumnValue(field, entity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityColumn that = (EntityColumn) o;
        return Objects.equals(type, that.type) && Objects.equals(columnName, that.columnName)
                && Objects.equals(columnLength, that.columnLength) && Objects.equals(columnIdOption, that.columnIdOption)
                && Objects.equals(columnOption, that.columnOption) && Objects.equals(columnValue, that.columnValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, columnName, columnLength, columnIdOption, columnOption, columnValue);
    }

    public boolean isIdGenerationFromDatabase() {
        return columnIdOption.isIdGenerationFromDatabase();
    }

    public boolean isOneToMany() {
        return columnOption.isOneToMany();
    }

    public FetchType getFetchType() {
        return columnOption.getFetchType();
    }

    public Class<?> getAssociationColumnType() {
        return columnOption.getAssociationColumnType();
    }

    public boolean isOneToManyAndLazy() {
        return columnOption.isOneToMany() && columnOption.getFetchType() == FetchType.LAZY;
    }
}
