package persistence.entity;

import common.ReflectionFieldAccessUtils;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.InsertQueryBuilder;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityCollectionPersister {

    private static final InsertQueryBuilder insertQueryBuilder = new InsertQueryBuilder();

    private final Logger logger = LoggerFactory.getLogger(EntityCollectionPersister.class);
    private final TableDefinition tableDefinition;

    private final JdbcTemplate jdbcTemplate;

    public EntityCollectionPersister(Class<?> elementClass, JdbcTemplate jdbcTemplate) {
        this.tableDefinition = new TableDefinition(elementClass);
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Object> insertCollection(Object parentEntity) {
        // TODO Metamodel
        final TableDefinition parentTableDefinition = new TableDefinition(parentEntity.getClass());
        final List<TableAssociationDefinition> associations = parentTableDefinition.getAssociations();
        final List<Object> childEntities = new ArrayList<>();

        associations.forEach(association -> {
            final Collection<?> associatedValues = parentTableDefinition.getIterableAssociatedValue(parentEntity, association);
            if (associatedValues instanceof Iterable<?> iterable) {
                iterable.forEach(entity -> {
                    Object result = doInsert(entity);
                    childEntities.add(result);
                });
            }
        });

        return childEntities;
    }

    private Object doInsert(Object entity) {
        final String query = insertQueryBuilder.build(entity);
        final Serializable id = jdbcTemplate.insertAndReturnKey(query);

        bindId(id, entity);
        return entity;
    }

    public Collection<Object> getChildCollections(Object parentEntity) {
        // TODO Metamodel
        final TableDefinition parentTableDefinition = new TableDefinition(parentEntity.getClass());
        final List<TableAssociationDefinition> associations = parentTableDefinition.getAssociations();
        final List<Object> childEntities = new ArrayList<>();

        associations.forEach(association -> {
            final Collection<?> associatedValues = parentTableDefinition.getIterableAssociatedValue(parentEntity, association);
            if (associatedValues instanceof Iterable<?> iterable) {
                iterable.forEach(childEntities::add);
            }
        });

        return childEntities;
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
}
