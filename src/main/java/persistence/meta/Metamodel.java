package persistence.meta;

import jdbc.JdbcTemplate;
import org.jetbrains.annotations.NotNull;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityPersister;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Metamodel {
    private final Map<Class<?>, TableDefinition> tableDefinitions;
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<TableAssociationDefinition, CollectionPersister> collectionPersisters;

    public Metamodel(List<Class<?>> entityClasses, JdbcTemplate jdbcTemplate) {
        this.tableDefinitions = collectTableDefinitions(entityClasses);
        this.entityPersisters = collectEntityPersisters(entityClasses, jdbcTemplate);
        this.collectionPersisters = collectCollectionPersisters(jdbcTemplate);
    }

    @NotNull
    private static Map<Class<?>, TableDefinition> collectTableDefinitions(List<Class<?>> entityClasses) {
        return entityClasses.stream().collect(
                Collectors.toUnmodifiableMap(
                        clazz -> clazz,
                        TableDefinition::new
                )
        );
    }

    @NotNull
    private Map<Class<?>, EntityPersister> collectEntityPersisters(List<Class<?>> entityClasses, JdbcTemplate jdbcTemplate) {
        return entityClasses.stream().collect(
                Collectors.toUnmodifiableMap(
                        clazz -> clazz,
                        clazz -> new EntityPersister(tableDefinitions.get(clazz), jdbcTemplate)
                )
        );
    }

    @NotNull
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

    public TableDefinition findTableDefinition(Class<?> clazz) {
        return tableDefinitions.get(clazz);
    }

    public EntityPersister findEntityPersister(Class<?> clazz) {
        return entityPersisters.get(clazz);
    }

    public CollectionPersister findCollectionPersister(TableAssociationDefinition association) {
        return collectionPersisters.get(association);
    }
}