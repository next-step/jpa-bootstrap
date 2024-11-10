package persistence.meta;

import database.DatabaseServer;
import persistence.session.EntityManagerFactory;
import persistence.sql.Dialect;
import persistence.sql.definition.ColumnDefinitionAware;
import persistence.sql.definition.TableDefinition;

import java.util.List;

public interface Metadata {
    EntityManagerFactory buildEntityManagerFactory();

    DatabaseServer getDatabase();

    Dialect getDialect();

    List<Class<?>> getEntityClasses();

    List<TableDefinition> findTableDefinitions();

    TableDefinition findTableDefinition(Class<?> entityClass);

    List<? extends ColumnDefinitionAware> getForeignKeys(Class<?> entityClass);
}
