package persistence.sql.context.impl;

import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import persistence.annoation.DynamicUpdate;
import persistence.sql.QueryBuilderFactory;
import persistence.sql.clause.Clause;
import persistence.sql.clause.DeleteQueryClauses;
import persistence.sql.clause.InsertColumnValueClause;
import persistence.sql.clause.UpdateQueryClauses;
import persistence.sql.common.util.NameConverter;
import persistence.sql.context.EntityPersister;
import persistence.sql.data.QueryType;
import persistence.sql.dml.Database;
import persistence.sql.dml.MetadataLoader;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class DefaultEntityPersister implements EntityPersister {
    private static final Logger logger = Logger.getLogger(DefaultEntityPersister.class.getName());
    private final Database database;
    private final NameConverter nameConverter;
    private final MetadataLoader<?> metadataLoader;

    public DefaultEntityPersister(Database database, NameConverter nameConverter, MetadataLoader<?> metadataLoader) {
        this.database = database;
        this.nameConverter = nameConverter;
        this.metadataLoader = metadataLoader;
    }

    @Override
    public Object insert(Object entity) {
        InsertColumnValueClause clause = InsertColumnValueClause.newInstance(entity, nameConverter);

        String insertQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.INSERT, metadataLoader, clause);
        Object id = database.executeUpdate(insertQuery);
        updatePrimaryKeyValue(entity, id);

        return entity;
    }

    @Override
    public Object insert(Object entity, Object parentEntity) {
        InsertColumnValueClause clause = InsertColumnValueClause.newInstance(entity, parentEntity, nameConverter);

        String insertQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.INSERT, metadataLoader, clause);
        logger.info("Entity: %s, Parent Entity: %s | insertQuery: %s".formatted(entity, parentEntity, insertQuery));

        Object id = database.executeUpdate(insertQuery);
        updatePrimaryKeyValue(entity, id);

        return entity;
    }

    @Override
    public void update(Object entity, Object snapshotEntity) {
        List<Field> updateTargetFields = getUpdateTargetFields(entity, snapshotEntity);
        UpdateQueryClauses updateQueryClauses = UpdateQueryClauses.builder(nameConverter)
                .where(entity, metadataLoader)
                .setColumnValues(entity, updateTargetFields, metadataLoader)
                .build();

        String mergeQuery = QueryBuilderFactory.getInstance()
                .buildQuery(QueryType.UPDATE, metadataLoader, updateQueryClauses.clauseArrays());
        database.executeUpdate(mergeQuery);
    }

    private List<Field> getUpdateTargetFields(Object entity, Object snapshotEntity) {
        if (metadataLoader.isClassAnnotationPresent(DynamicUpdate.class) && snapshotEntity != null) {
            return extractDiffFields(entity, snapshotEntity);
        }

        return metadataLoader.getFieldAllByPredicate(field -> !field.isAnnotationPresent(Id.class) && !isAssociationField(field));
    }

    List<Field> extractDiffFields(Object entity, Object snapshotEntity) {
        return metadataLoader.getFieldAllByPredicate(field -> {
            Object entityValue = Clause.extractValue(field, entity);
            Object snapshotValue = Clause.extractValue(field, snapshotEntity);

            if (entityValue == null && snapshotValue == null) {
                return false;
            }

            if (entityValue == null || snapshotValue == null) {
                return true;
            }

            return !entityValue.equals(snapshotValue);
        });
    }

    private boolean isAssociationField(Field field) {
        final List<Class<?>> associationAnnotations = List.of(OneToMany.class, ManyToMany.class, ManyToOne.class, OneToOne.class);

        return Arrays.stream(field.getDeclaredAnnotations())
                .anyMatch(annotation -> associationAnnotations.contains(annotation.annotationType()));
    }

    @Override
    public void delete(Object entity) {
        DeleteQueryClauses deleteQueryClauses = DeleteQueryClauses.builder(nameConverter)
                .where(entity, metadataLoader)
                .build();

        String removeQuery = QueryBuilderFactory.getInstance().buildQuery(QueryType.DELETE, metadataLoader,
                deleteQueryClauses.clauseArrays());

        database.executeUpdate(removeQuery);
    }


    private void updatePrimaryKeyValue(Object entity, Object id) {
        Field primaryKeyField = metadataLoader.getPrimaryKeyField();
        primaryKeyField.setAccessible(true);

        try {
            primaryKeyField.set(entity, id);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
