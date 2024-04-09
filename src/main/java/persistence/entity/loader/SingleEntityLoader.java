package persistence.entity.loader;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import persistence.ReflectionUtils;
import persistence.entity.Proxy.ProxyFactory;
import persistence.entity.SingleEntityRowMapper;
import persistence.model.*;
import persistence.sql.QueryException;
import persistence.sql.dml.*;
import persistence.sql.mapping.*;

import java.util.*;
import java.util.stream.Collectors;

public class SingleEntityLoader implements EntityLoader {

    private final TableBinder tableBinder;
    private final PersistentClassMapping persistentClassMapping;
    private final ProxyFactory proxyFactory;
    private final DmlQueryBuilder dmlQueryBuilder;
    private final JdbcTemplate jdbcTemplate;

    public SingleEntityLoader(final TableBinder tableBinder, final PersistentClassMapping persistentClassMapping, final ProxyFactory proxyFactory, final DmlQueryBuilder dmlQueryBuilder, final JdbcTemplate jdbcTemplate) {
        this.tableBinder = tableBinder;
        this.persistentClassMapping = persistentClassMapping;
        this.proxyFactory = proxyFactory;
        this.dmlQueryBuilder = dmlQueryBuilder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public <T> List<T> load(final PersistentClass<T> persistentClass, final Object key) {
        final Select select = generateSelect(persistentClass, key);

        final String selectQuery = dmlQueryBuilder.buildSelectQuery(select);
        log.debug("\n" + selectQuery);

        final Map<Boolean, List<EntityJoinField>> joinFieldsMap = persistentClass.getFields()
                .stream()
                .filter(AbstractEntityField::isJoinField)
                .map(joinField -> (EntityJoinField) joinField)
                .collect(Collectors.partitioningBy(EntityJoinField::isEager));

        final Optional<EntityJoinField> eagerJoinField = joinFieldsMap.getOrDefault(true, Collections.emptyList())
                .stream().findFirst();

        final List<EntityJoinField> lazyJoinFields = joinFieldsMap.getOrDefault(false, Collections.emptyList());

        final List<T> entities = getEntities(persistentClass, eagerJoinField, selectQuery);

        if (!lazyJoinFields.isEmpty()) {
            setEntityLazyFields(persistentClass, entities, lazyJoinFields);
        }

        return entities;
    }

    private <T> void setEntityLazyFields(final PersistentClass<T> persistentClass, final List<T> entities, final List<EntityJoinField> lazyJoinFields) {
        final CollectionEntityLoader collectionEntityLoader = new CollectionEntityLoader(jdbcTemplate);
        final EntityId entityId = persistentClass.getEntityId();

        entities.forEach(entity -> {
            final Object idValue = ReflectionUtils.getFieldValue(entityId.getField(), entity);

            lazyJoinFields.forEach(joinField -> {
                final PersistentClass<?> joinedPersistentClass = persistentClassMapping.getPersistentClass(joinField.getEntityClass());
                final Select joinedEntitySelect = generateSelect(joinedPersistentClass, joinField.getJoinedColumnName(), idValue);
                final String joinedTableSelectQuery = dmlQueryBuilder.buildSelectQuery(joinedEntitySelect);
                final Collection<?> values =
                        proxyFactory.generateCollectionProxy(joinedPersistentClass, joinField.getFieldClass(), collectionEntityLoader, joinField.getEntityClass(), joinedTableSelectQuery);

                ReflectionUtils.setCollectionFieldValue(joinField.getField(), entity, values);
            });
        });
    }

    private <T> List<T> getEntities(final PersistentClass<T> persistentClass, final Optional<EntityJoinField> eagerJoinField, final String query) {
        if (eagerJoinField.isPresent()) {
            final CollectionEntityLoader collectionEntityLoader = new CollectionEntityLoader(jdbcTemplate);
            return collectionEntityLoader.queryWithEagerColumn(persistentClass.getEntityClass(), eagerJoinField.get(), persistentClassMapping.getCollectionPersistentClassBinder(), query);
        }

        final RowMapper<T> rowMapper = new SingleEntityRowMapper<>(persistentClass);

        return jdbcTemplate.query(query, rowMapper)
                .stream()
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toUnmodifiableList());
    }

    private <T> Select generateSelect(final PersistentClass<T> persistentClass, final Object key) {
        final Table table = tableBinder.createTable(persistentClass, persistentClassMapping);

        final Select select = new Select(table);

        if (Objects.nonNull(key)) {
            select.addWhere(generateIdColumnWhere(table, key));
        }

        return select;
    }

    private Where generateIdColumnWhere(final Table table, final Object key) {
        final Column idColumn = findIdColumnInPrimaryKey(table.getPrimaryKey());
        idColumn.setValue(key);

        return new Where(idColumn, idColumn.getValue(), LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ));
    }

    private Column findIdColumnInPrimaryKey(final PrimaryKey primaryKey) {
        return primaryKey.getColumns()
                .stream()
                .findFirst()
                .orElseThrow(() -> new QueryException("not found id column"));
    }

    private <T> Select generateSelect(final PersistentClass<T> persistentClass, final String columnName, final Object value) {
        final Table table = tableBinder.createTable(persistentClass, persistentClassMapping);

        final Select select = new Select(table);
        final Value columnValue = new Value(value.getClass(), ColumnTypeMapper.getInstance().toSqlType(value.getClass()), value);

        select.addWhere(new Where(table.getName(), columnName, columnValue, LogicalOperator.NONE, new ComparisonOperator(ComparisonOperator.Comparisons.EQ)));

        return select;
    }
}
