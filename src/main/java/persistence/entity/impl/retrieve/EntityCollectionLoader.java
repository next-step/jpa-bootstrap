package persistence.entity.impl.retrieve;

import java.sql.Connection;
import java.util.List;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.impl.EntityRowMapper;
import persistence.entity.proxy.CollectionProxyWrapper;
import persistence.sql.dml.clause.operator.EqualOperator;
import persistence.sql.dml.clause.predicate.WherePredicate;
import persistence.sql.dml.statement.SelectStatementBuilder;
import persistence.sql.schema.meta.ColumnMeta;
import persistence.sql.schema.meta.EntityClassMappingMeta;
import persistence.sql.schema.meta.EntityObjectMappingMeta;
import registry.EntityMetaRegistry;

public class EntityCollectionLoader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final JdbcTemplate jdbcTemplate;
    private final CollectionObjectMapper collectionObjectMapper;
    private final CollectionProxyWrapper collectionProxyWrapper;
    private final EntityMetaRegistry entityMetaRegistry;

    public EntityCollectionLoader(Connection connection, EntityMetaRegistry entityMetaRegistry) {
        this.jdbcTemplate = new JdbcTemplate(connection);
        this.entityMetaRegistry = entityMetaRegistry;
        this.collectionObjectMapper = new CollectionObjectMapper();
        this.collectionProxyWrapper = new CollectionProxyWrapper();
    }

    public <T> T loadCollection(Class<T> clazz, Object instance) {
        final EntityClassMappingMeta entityClassMappingMeta = entityMetaRegistry.getEntityMeta(clazz);
        final EntityObjectMappingMeta objectMappingMeta = EntityObjectMappingMeta.of(instance, entityClassMappingMeta);
        final List<ColumnMeta> relationColumnMetaList = entityClassMappingMeta.getRelationColumnMetaList();

        for (ColumnMeta columnMeta : relationColumnMetaList) {
            load(instance, objectMappingMeta, columnMeta);
        }

        return clazz.cast(instance);
    }

    private void load(Object instance, EntityObjectMappingMeta objectMappingMeta, ColumnMeta columnMeta) {
        if (columnMeta.isEagerLoading()) {
            eagerLoad(instance, columnMeta, objectMappingMeta);
            return;
        }

        lazyLoad(instance, columnMeta, objectMappingMeta);
    }

    private void eagerLoad(Object instance, ColumnMeta columnMeta, EntityObjectMappingMeta objectMappingMeta) {
        final List<?> loadedChildList = selectRelation(columnMeta, objectMappingMeta);

        collectionObjectMapper.mappingFieldRelation(instance, columnMeta, loadedChildList);
    }

    private void lazyLoad(Object instance, ColumnMeta columnMeta, EntityObjectMappingMeta objectMappingMeta) {
        final Object collectionProxy = collectionProxyWrapper.wrap(
            columnMeta.getColumnType(),
            () -> selectRelation(columnMeta, objectMappingMeta)
        );

        collectionObjectMapper.mappingFieldRelation(instance, columnMeta, collectionProxy);
    }

    private List<?> selectRelation(ColumnMeta columnMeta, EntityObjectMappingMeta objectMappingMeta) {
        final EntityClassMappingMeta joinEntityClassMappingMeta = entityMetaRegistry.getEntityMeta(columnMeta.getJoinColumnTableType());

        final String selectRelationSql = SelectStatementBuilder.builder()
            .selectFrom(joinEntityClassMappingMeta)
            .where(WherePredicate.of(columnMeta.getJoinColumnName(), objectMappingMeta.getIdValue(), new EqualOperator()))
            .build();

        logger.info(selectRelationSql);

        return jdbcTemplate.query(
            selectRelationSql,
            new EntityRowMapper<>(columnMeta.getJoinColumnTableType(), entityMetaRegistry)
        );
    }
}
