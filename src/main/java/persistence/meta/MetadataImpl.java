package persistence.meta;

import bootstrap.ClassFileProcessor;
import bootstrap.EntityComponentScanner;
import bootstrap.FileSystemExplorer;
import database.DatabaseServer;
import persistence.session.EntityManagerFactory;
import persistence.session.EntityManagerFactoryImpl;
import persistence.session.ThreadLocalCurrentSessionContext;
import persistence.sql.Dialect;
import persistence.sql.H2Dialect;
import persistence.sql.definition.ColumnDefinitionAware;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class MetadataImpl implements Metadata {

    private final DatabaseServer database;
    private final List<Class<?>> entityClasses;
    private final Map<Class<?>, TableDefinition> tableDefinitions;
    private final List<TableAssociationDefinition> associations;

    public MetadataImpl(DatabaseServer database) {
        final Properties properties = new Properties();
        final String packageName = loadPackageName(properties);

        this.entityClasses = scanEntityClasses(packageName);
        this.database = database;
        this.tableDefinitions = collectTableDefinitions(entityClasses);
        this.associations = collectAssociations(entityClasses);
    }

    private static List<Class<?>> scanEntityClasses(String packageName) {
        List<Class<?>> entityClasses;
        try {
            entityClasses = new EntityComponentScanner(
                    new FileSystemExplorer(),
                    new ClassFileProcessor()
            ).scan(packageName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return entityClasses;
    }

    private String loadPackageName(Properties properties) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties file", e);
        }

        return properties.getProperty("entity.package", "domain");
    }

    private Map<Class<?>, TableDefinition> collectTableDefinitions(List<Class<?>> entityClasses) {
        return entityClasses.stream().collect(
                Collectors.toUnmodifiableMap(
                        clazz -> clazz,
                        TableDefinition::new
                )
        );
    }

    private List<TableAssociationDefinition> collectAssociations(List<Class<?>> entityClasses) {
        return entityClasses.stream().flatMap(clazz ->
                        tableDefinitions.get(clazz).getAssociations().stream()
                )
                .toList();
    }

    @Override
    public EntityManagerFactory buildEntityManagerFactory() {
        try {
            return new EntityManagerFactoryImpl(
                    new ThreadLocalCurrentSessionContext(),
                    this
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public DatabaseServer getDatabase() {
        return database;
    }

    @Override
    public Dialect getDialect() {
        return new H2Dialect();
    }

    @Override
    public List<Class<?>> getEntityClasses() {
        return entityClasses;
    }

    @Override
    public List<TableDefinition> findTableDefinitions() {
        return tableDefinitions.values().stream().toList();
    }

    @Override
    public TableDefinition findTableDefinition(Class<?> entityClass) {
        return tableDefinitions.get(entityClass);
    }

    @Override
    public List<? extends ColumnDefinitionAware> getForeignKeys(Class<?> entityClass) {
        for (TableAssociationDefinition association : associations) {
            if (association.getAssociatedEntityClass().equals(entityClass)) {
                final TableDefinition tableDefinition = findTableDefinition(association.getParentEntityClass());
                String joinColumnName = association.getJoinColumnName();

                return tableDefinition.getColumns().stream()
                        .filter(column -> column.getDatabaseColumnName().equals(joinColumnName))
                        .toList();
            }
        }

        return List.of();
    }
}
