package persistence.meta;

import jdbc.JdbcTemplate;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityPersister;
import persistence.sql.definition.ColumnDefinitionAware;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Metamodel {
    private final Map<Class<?>, TableDefinition> tableDefinitions;
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<TableAssociationDefinition, CollectionPersister> collectionPersisters;
    private final List<TableAssociationDefinition> associations;

    public Metamodel(List<Class<?>> entityClasses, JdbcTemplate jdbcTemplate) {
        this.tableDefinitions = collectTableDefinitions(entityClasses);
        this.associations = collectAssociations(entityClasses);
        this.entityPersisters = collectEntityPersisters(entityClasses, jdbcTemplate);
        this.collectionPersisters = collectCollectionPersisters(jdbcTemplate);
    }

    private static Map<Class<?>, TableDefinition> collectTableDefinitions(List<Class<?>> entityClasses) {
        return entityClasses.stream().collect(
                Collectors.toUnmodifiableMap(
                        clazz -> clazz,
                        TableDefinition::new
                )
        );
    }

    private Map<Class<?>, EntityPersister> collectEntityPersisters(List<Class<?>> entityClasses, JdbcTemplate jdbcTemplate) {
        return entityClasses.stream().collect(
                Collectors.toUnmodifiableMap(
                        clazz -> clazz,
                        clazz -> new EntityPersister(tableDefinitions.get(clazz), jdbcTemplate)
                )
        );
    }

    private Map<TableAssociationDefinition, CollectionPersister> collectCollectionPersisters(JdbcTemplate jdbcTemplate) {
        return tableDefinitions.values().stream().flatMap(tableDefinition ->
                        tableDefinition.getAssociations().stream()
                )
                .filter(TableAssociationDefinition::isCollection)
                .collect(
                        Collectors.toUnmodifiableMap(
                                association -> association,
                                association -> new CollectionPersister(
                                        entityPersisters.get(association.getParentEntityClass()),
                                        entityPersisters.get(association.getAssociatedEntityClass()),
                                        jdbcTemplate
                                )
                        )
                );
    }

    private List<TableAssociationDefinition> collectAssociations(List<Class<?>> entityClasses) {
        return entityClasses.stream().flatMap(clazz ->
                        tableDefinitions.get(clazz).getAssociations().stream()
                )
                .toList();
    }

    public TableDefinition findTableDefinition(Class<?> clazz) {
        return tableDefinitions.get(clazz);
    }

    public EntityPersister findEntityPersister(Class<?> clazz) {
        return entityPersisters.get(clazz);
    }

    public CollectionPersister findCollectionPersister(TableAssociationDefinition association) {
        return collectionPersisters.get(association);
    }

    public List<TableAssociationDefinition> resolveEagerAssociation(Class<?> entityClass) {
        return entityPersisters.get(entityClass).getAssociations()
                .stream().filter(TableAssociationDefinition::isEager)
                .collect(Collectors.toList());
    }

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
