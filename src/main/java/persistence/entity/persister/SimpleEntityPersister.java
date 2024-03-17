package persistence.entity.persister;

import java.util.Collection;
import java.util.List;
import jdbc.JdbcTemplate;
import persistence.sql.dml.DmlGenerator;
import persistence.sql.meta.Table;

public class SimpleEntityPersister<T> implements EntityPersister<T> {

    private final DmlGenerator dmlGenerator;
    private final JdbcTemplate jdbcTemplate;
    private final Class<T> type;

    private SimpleEntityPersister(JdbcTemplate jdbcTemplate, Class<T> type) {
        this.dmlGenerator = DmlGenerator.getInstance();
        this.jdbcTemplate = jdbcTemplate;
        this.type = type;
    }

    public static <T> SimpleEntityPersister<T> of(JdbcTemplate jdbcTemplate, Class<T> type) {
        return new SimpleEntityPersister<>(jdbcTemplate, type);
    }

    @Override
    public boolean update(T entity) {
        return jdbcTemplate.executeUpdate(dmlGenerator.generateUpdateQuery(entity)) > 0;
    }

    @Override
    public void insert(T entity) {
        Object id = jdbcTemplate.executeInsert(dmlGenerator.generateInsertQuery(entity));
        Table table = Table.getInstance(entity.getClass());
        table.setIdValue(entity, id);
        insertRelation(table, entity);
    }

    @Override
    public void delete(T entity) {
        jdbcTemplate.executeUpdate(dmlGenerator.generateDeleteQuery(entity));
    }

    private void insertRelation(Table root, Object entity) {

        List<Object> relatedEntities = root.getRelationValues(entity);
        relatedEntities.stream()
            .map(relatedEntity -> ((Collection<Object>) relatedEntity))
            .flatMap(Collection::stream)
            .forEach(relatedEntity -> {
                Object subId = jdbcTemplate.executeInsert(dmlGenerator.generateInsertQuery(relatedEntity, entity));
                Table table = Table.getInstance(relatedEntity.getClass());
                table.setIdValue(relatedEntity, subId);
            });
    }
}
