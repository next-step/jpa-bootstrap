package persistence.entity;

import common.ReflectionFieldAccessUtils;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.DeleteQueryBuilder;
import persistence.sql.dml.query.InsertQueryBuilder;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class EntityPersister {
    private static final Long DEFAULT_ID_VALUE = 0L;
    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();
    private static final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();
    private static final DeleteQueryBuilder deleteQueryBuilder = new DeleteQueryBuilder();

    private final Logger logger = LoggerFactory.getLogger(EntityPersister.class);
    private final TableDefinition tableDefinition;

    private final JdbcTemplate jdbcTemplate;

    public EntityPersister(Class<?> entityClass, JdbcTemplate jdbcTemplate) {
        this.tableDefinition = new TableDefinition(entityClass);
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean hasId(Object entity) {
        return tableDefinition.hasId(entity);
    }

    public Serializable getEntityId(Object entity) {
        if (tableDefinition.hasId(entity)) {
            return tableDefinition.getIdValue(entity);
        }

        return DEFAULT_ID_VALUE;
    }

    public Object insert(Object entity) {
        final String query = insertQueryBuilder.build(entity);
        final Serializable id = jdbcTemplate.insertAndReturnKey(query);

        bindId(id, entity);

        tableDefinition.getAssociations().forEach(association -> {
                    if (association.isCollection()) {
                        // TODO Metamodel
                        final EntityCollectionPersister entityCollectionPersister = new EntityCollectionPersister(
                                association.getEntityClass(), jdbcTemplate);

                        final Collection<Object> childEntities = entityCollectionPersister.insertCollection(entity);
                        childEntities.forEach(child -> updateAssociatedColumns(entity, child));
                    }
                }
        );

        return entity;
    }

    private void updateAssociatedColumns(Object parent, Object child) {
        // TODO Metamodel
        final TableDefinition parentDefinition = new TableDefinition(parent.getClass());
        final TableDefinition childDefinition = new TableDefinition(child.getClass());
        String updateQuery = updateQueryBuilder.build(parent, child, parentDefinition, childDefinition);

        jdbcTemplate.execute(updateQuery);
    }

    public Collection<Object> getChildCollections(Object parentEntity) {
        // TODO Metamodel

        for (TableAssociationDefinition association : tableDefinition.getAssociations()) {
            if (association.isCollection()) {
                // TODO Metamodel
                final EntityCollectionPersister entityCollectionPersister = new EntityCollectionPersister(
                        association.getEntityClass(), jdbcTemplate);
                return entityCollectionPersister.getChildCollections(parentEntity);
            }
        }

        return new ArrayList<>();
    }

    private void bindId(Serializable id, Object entity) {
        try {
            final Field idField = tableDefinition.getEntityClass()
                    .getDeclaredField(tableDefinition.getIdFieldName());

            ReflectionFieldAccessUtils.accessAndSet(entity, idField, id);
        } catch (ReflectiveOperationException e) {
            logger.error("Failed to copy row to {}", entity.getClass().getName(), e);
        }
    }

    public void update(Object entity) {
        final String query = updateQueryBuilder.build(entity, tableDefinition);
        jdbcTemplate.execute(query);
    }

    public void delete(Object entity) {
        String query = deleteQueryBuilder.build(entity);
        jdbcTemplate.execute(query);
    }

}
