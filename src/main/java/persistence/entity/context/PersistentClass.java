package persistence.entity.context;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.ColumnsMetadata;
import database.mapping.EntityAssociationMetadata;
import database.mapping.TableMetadata;
import database.mapping.column.EntityColumn;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import database.mapping.rowmapper.JoinedRowMapper;
import database.mapping.rowmapper.SingleRowMapperFactory;
import jdbc.RowMapper;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersistentClass<T> {

    private final Class<T> mappedClass;
    private final String mappedClassName;
    private final String tableName;
    private final String primaryKeyName;
    private final List<String> generalColumnNames;
    private final boolean requiresIdWhenInserting;
    private final PrimaryKeyEntityColumn primaryKey;
    private final List<GeneralEntityColumn> generalColumns;
    private final List<EntityColumn> allEntityColumns;

    private final EntityAssociationMetadata entityAssociationMetadata;
    private final boolean hasAssociation;
    private final List<Association> associations;
    private final ColumnsMetadata columnsMetadata;

    private final RowMapper<T> rowMapper;
    private final JoinedRowMapper<T> joinedRowMapper;

    public static <T> PersistentClass<T> fromInternal(Class<T> mappedClass, Dialect dialect) {
        TableMetadata tableMetadata = new TableMetadata(mappedClass);
        ColumnsMetadata columnsMetadata = ColumnsMetadata.fromClass(mappedClass);
        EntityAssociationMetadata entityAssociationMetadata = new EntityAssociationMetadata(mappedClass);

        return new PersistentClass<>(
                mappedClass,
                mappedClass.getName(),
                tableMetadata.getTableName(),
                ColumnsMetadata.fromClass(mappedClass),
                columnsMetadata.getPrimaryKey(),
                columnsMetadata.getPrimaryKey().getColumnName(),
                columnsMetadata.isRequiredId(),
                columnsMetadata.getGeneralColumns(),
                columnsMetadata.getGeneralColumnNames(),
                columnsMetadata.getAllEntityColumns(),
                entityAssociationMetadata,
                entityAssociationMetadata.hasAssociations(),
                entityAssociationMetadata.getAssociations(),
                dialect
        );
    }

    private PersistentClass(Class<T> mappedClass,
                            String mappedClassName,
                            String tableName,
                            ColumnsMetadata columnsMetadata,
                            PrimaryKeyEntityColumn primaryKey,
                            String primaryKeyName,
                            boolean requiresIdWhenInserting,
                            List<GeneralEntityColumn> generalColumns,
                            List<String> generalColumnNames,
                            List<EntityColumn> allEntityColumns,
                            EntityAssociationMetadata entityAssociationMetadata,
                            boolean hasAssociation,
                            List<Association> associations,
                            Dialect dialect) {

        this.mappedClass = mappedClass;

        this.tableName = tableName;
        this.mappedClassName = mappedClassName;

        this.columnsMetadata = columnsMetadata;
        this.primaryKeyName = primaryKeyName;
        this.generalColumnNames = generalColumnNames;
        this.requiresIdWhenInserting = requiresIdWhenInserting;
        this.primaryKey = primaryKey;
        this.generalColumns = generalColumns;
        this.allEntityColumns = allEntityColumns;

        this.entityAssociationMetadata = entityAssociationMetadata;
        this.hasAssociation = hasAssociation;
        this.associations = associations;

        this.rowMapper = SingleRowMapperFactory.create(this, dialect);
        this.joinedRowMapper = new JoinedRowMapper<>(this, dialect);
    }

    public Class<T> getMappedClass() {
        return mappedClass;
    }

    public String getMappedClassName() {
        return mappedClassName;
    }

    public boolean hasAssociation() {
        return hasAssociation;
    }

    public String getTableName() {
        return tableName;
    }

    public String getPrimaryKeyName() {
        return primaryKeyName;
    }

    public List<String> getGeneralColumnNames() {
        return generalColumnNames;
    }

    public boolean requiresIdWhenInserting() {
        return requiresIdWhenInserting;
    }

    public PrimaryKeyEntityColumn getPrimaryKey() {
        return primaryKey;
    }

    public List<GeneralEntityColumn> getGeneralColumns() {
        return generalColumns;
    }

    public List<Association> getAssociations() {
        return associations;
    }

    public List<EntityColumn> getAllEntityColumns() {
        return allEntityColumns;
    }

    public RowMapper<T> getRowMapper() {
        return rowMapper;
    }

    public JoinedRowMapper<T> getJoinedRowMapper() {
        return joinedRowMapper;
    }

    public T newEntity() {
        try {
            return mappedClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public Field getFieldByFieldName(String fieldName) {
        return columnsMetadata.getFieldByFieldName(fieldName);
    }

    public Field getFieldByColumnName(String columnName) {
        return columnsMetadata.getFieldByColumnName(columnName);
    }

    public List<String> getAllColumnNamesWithAssociations(List<Class<?>> entityClasses) {
        List<String> allColumnsWithAssociation = new ArrayList<>();

        allColumnsWithAssociation.add(primaryKeyName);
        allColumnsWithAssociation.addAll(generalColumnNames);

        List<Association> associationsRelatedTo = this.getAssociationsRelatedTo(entityClasses);
        allColumnsWithAssociation.addAll(associationsRelatedTo.stream()
                                                 .map(Association::getForeignKeyColumnName)
                                                 .collect(Collectors.toList()));
        return allColumnsWithAssociation;
    }

    public List<Association> getAssociationsRelatedTo(List<Class<?>> entityClasses) {
        return entityAssociationMetadata.getAssociationsRelatedTo(entityClasses);
    }

    public Long getRowId(Object entity) {
        return (Long) primaryKey.getValue(entity);
    }
}
