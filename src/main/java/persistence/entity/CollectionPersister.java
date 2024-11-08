package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.dml.query.UpdateQueryBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CollectionPersister {
    private static final UpdateQueryBuilder updateQueryBuilder = new UpdateQueryBuilder();

    private final EntityPersister parentPersister;
    private final EntityPersister elementPersister;
    private final JdbcTemplate jdbcTemplate;

    public CollectionPersister(EntityPersister parentPersister,
                               EntityPersister elementPersister,
                               JdbcTemplate jdbcTemplate) {
        this.parentPersister = parentPersister;
        this.elementPersister = elementPersister;
        this.jdbcTemplate = jdbcTemplate;
    }

    public Collection<Object> insertCollection(Object parentEntity, TableAssociationDefinition association) {
        final List<Object> childEntities = new ArrayList<>();
        final Collection<?> associatedValues = parentPersister.getIterableAssociatedValue(parentEntity, association);
        if (associatedValues instanceof Iterable<?> iterable) {
            iterable.forEach(entity -> {
                Object result = insert(entity);
                childEntities.add(result);
            });
        }

        childEntities.forEach(childEntity -> updateAssociatedColumns(parentEntity, childEntity));
        return childEntities;
    }

    private void updateAssociatedColumns(Object parentEntity, Object childEntity) {
        final String joinColumnName = parentPersister.getJoinColumnName(elementPersister.getEntityClass());
        final Object joinColumnValue = parentPersister.getColumnValue(parentEntity, joinColumnName);

        final String sql = updateQueryBuilder.build(
                elementPersister.getTableName(),
                elementPersister.getIdName(),
                elementPersister.getEntityId(childEntity),
                Map.of(joinColumnName, joinColumnValue)
        );
        jdbcTemplate.execute(sql);
    }

    private Object insert(Object entity) {
        return elementPersister.insert(entity);
    }

}
