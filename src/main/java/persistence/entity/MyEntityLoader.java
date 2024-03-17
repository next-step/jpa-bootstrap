package persistence.entity;

import jdbc.JdbcTemplate;
import jdbc.RowMapper;
import jdbc.RowMapperFactory;
import persistence.proxy.MyProxyFactory;
import persistence.proxy.ProxyFactory;
import persistence.sql.dml.SelectQueryBuilder;
import persistence.sql.meta.AssociationTable;
import persistence.sql.meta.Table;

import java.util.List;

public class MyEntityLoader<T> implements EntityLoader<T> {

    private final JdbcTemplate jdbcTemplate;
    private final EntityMeta<T> entityMeta;
    private final SelectQueryBuilder selectQueryBuilder;
    private final ProxyFactory proxyFactory;

    public MyEntityLoader(JdbcTemplate jdbcTemplate, EntityMeta<T> entityMeta) {
        this.jdbcTemplate = jdbcTemplate;
        this.entityMeta = entityMeta;
        this.selectQueryBuilder = SelectQueryBuilder.getInstance();
        this.proxyFactory = new MyProxyFactory(jdbcTemplate);
    }

    @Override
    public T find(Object Id) {
        Table table = entityMeta.getTable();
        if (!table.containsAssociation()) {
            String query = selectQueryBuilder.build(table, Id);
            RowMapper<T> rowMapper = RowMapperFactory.create(entityMeta.getClazz());
            return jdbcTemplate.queryForObject(query, rowMapper);
        }
        String query = selectQueryBuilder.buildWithJoin(table, Id);
        RowMapper<T> rowMapper = RowMapperFactory.create(entityMeta.getClazz());
        T object = jdbcTemplate.queryForObject(query, rowMapper);
        if (table.containsLazyAssociation()) {
            setProxyForLazyAssociationTables(table.getAssociationTables(), object);
        }
        return object;
    }

    private void setProxyForLazyAssociationTables(List<AssociationTable> associationTables, T object) {
        for (AssociationTable associationTable : associationTables) {
            setProxy(associationTable, object);
        }
    }

    private void setProxy(AssociationTable associationTable, T object) {
        try {
            Object proxy = proxyFactory.createProxy(associationTable.getClazz());
            associationTable.getField().setAccessible(true);
            associationTable.getField().set(object, proxy);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Access denied to set proxy for lazy association table field");
        } finally {
            associationTable.getField().setAccessible(false);
        }
    }
}
