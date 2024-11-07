package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EntityCollectionPersister {
    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

    private final TableDefinition parentTableDefinition;
    private final TableDefinition elementTableDefinition;

    private final InsertExecutor elementInsertExecutor;

    private final JdbcTemplate jdbcTemplate;

    public EntityCollectionPersister(TableDefinition parentTableDefinition,
                                     TableDefinition elementTableDefinition,
                                     JdbcTemplate jdbcTemplate) {
        this.parentTableDefinition = parentTableDefinition;
        this.elementTableDefinition = elementTableDefinition;
        this.jdbcTemplate = jdbcTemplate;
        this.elementInsertExecutor = new InsertExecutor(jdbcTemplate, elementTableDefinition);
    }

    public Collection<Object> insertCollection(Object parentEntity, TableAssociationDefinition association) {
        final List<Object> childEntities = new ArrayList<>();
        final Collection<?> associatedValues = parentTableDefinition.getIterableAssociatedValue(parentEntity, association);
        if (associatedValues instanceof Iterable<?> iterable) {
            iterable.forEach(entity -> {
                Object result = doInsert(entity);
                childEntities.add(result);
            });
        }

        childEntities.forEach(childEntity -> updateAssociatedColumns(parentEntity, childEntity));
        return childEntities;
    }

    private void updateAssociatedColumns(Object parentEntity, Object childEntity) {
        final String sql = updateQueryBuilder.build(parentEntity, childEntity, parentTableDefinition, elementTableDefinition);
        jdbcTemplate.execute(sql);
    }

    private Object doInsert(Object entity) {
        return elementInsertExecutor.insertAndBindKey(entity, elementTableDefinition);
    }

}
