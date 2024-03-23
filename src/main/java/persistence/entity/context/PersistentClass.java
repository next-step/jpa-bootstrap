package persistence.entity.context;

import database.dialect.Dialect;
import database.mapping.Association;
import database.mapping.ColumnsMetadata;
import database.mapping.EntityAssociationMetadata;
import database.mapping.TableMetadata;
import database.mapping.column.EntityColumn;
import database.mapping.column.GeneralEntityColumn;
import database.mapping.column.PrimaryKeyEntityColumn;
import database.sql.ddl.Create;
import persistence.bootstrap.MetadataImpl;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PersistentClass<T> {

    private final Class<T> mappedClass;
    private final List<Class<?>> allEntities;
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

    public static <T> PersistentClass<T> from(Class<T> mappedClass, MetadataImpl metadata) {
        TableMetadata tableMetadata = new TableMetadata(mappedClass);
        ColumnsMetadata columnsMetadata = ColumnsMetadata.fromClass(mappedClass);
        EntityAssociationMetadata entityAssociationMetadata = new EntityAssociationMetadata(mappedClass);

        List<Class<?>> allEntities = metadata.getComponents();
        return new PersistentClass<>(mappedClass,
                                     mappedClass.getName(),
                                     allEntities,
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
                                     entityAssociationMetadata.getAssociations()
        );
    }

    @Deprecated
    public static <T> PersistentClass<T> from(Class<T> clazz) {
        return from(clazz, MetadataImpl.INSTANCE);
    }

    private PersistentClass(Class<T> mappedClass,
                            String mappedClassName,
                            List<Class<?>> allEntities,
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
                            List<Association> associations) {

        this.mappedClass = mappedClass;
        this.allEntities = allEntities;

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

    public T newInstance() {
        try {
            return mappedClass.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getAllColumnNamesWithAssociations(List<Class<?>> allEntities) {
        List<String> allColumnsWithAssociation = new ArrayList<>();

        allColumnsWithAssociation.add(primaryKeyName);
        allColumnsWithAssociation.addAll(generalColumnNames);
        allColumnsWithAssociation.addAll(this.getAssociationsRelatedTo(allEntities).stream()
                                                 .map(Association::getForeignKeyColumnName)
                                                 .collect(Collectors.toList()));
        return allColumnsWithAssociation;
    }

    public Field getFieldByFieldName(String fieldName) {
        return columnsMetadata.getFieldByFieldName(fieldName);
    }

    public List<Association> getAssociationsRelatedTo(List<Class<?>> entities) {
        return entityAssociationMetadata.getAssociationsRelatedTo(entities);
    }

    public List<Association> getAssociationsRelatedTo() {
        return entityAssociationMetadata.getAssociationsRelatedTo(allEntities);
    }

    public Long getRowId(Object entity) {
        return columnsMetadata.getPrimaryKeyValue(entity);
    }

    public Field getFieldByColumnName(String columnName) {
        return columnsMetadata.getFieldByColumnName(columnName);
    }

    public Long getPrimaryKeyValue(Object entity) {
        return columnsMetadata.getPrimaryKeyValue(entity);
    }

    public String createQuery(Dialect dialect) {
        return Create.from(this, dialect).buildQuery();
    }
}
