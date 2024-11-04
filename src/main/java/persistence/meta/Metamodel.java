package persistence.meta;

import jdbc.EagerFetchRowMapper;
import jdbc.JdbcTemplate;
import jdbc.LazyFetchRowMapper;
import jdbc.RowMapper;
import org.jetbrains.annotations.NotNull;
import persistence.entity.EntityCollectionPersister;
import persistence.entity.EntityPersister;
import persistence.sql.definition.TableAssociationDefinition;
import persistence.sql.definition.TableDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Metamodel {
    private final Map<Class<?>, TableDefinition> tableDefinitions;
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<TableAssociationDefinition, EntityCollectionPersister> entityCollectionPersisters;

    private final Map<Class<?>, EagerFetchRowMapper<?>> eagerFetchRowMappers;
    private final Map<Class<?>, LazyFetchRowMapper<?>> lazyFetchRowMappers;

    public Metamodel(List<Class<?>> entityClasses, JdbcTemplate jdbcTemplate) {
        this.tableDefinitions = collectTableDefinitions(entityClasses);
        this.entityPersisters = collectEntityPersisters(entityClasses, jdbcTemplate);
        this.entityCollectionPersisters = collectCollectionPersisters(jdbcTemplate);
        this.eagerFetchRowMappers = collectEagerFetchRowMappers(entityClasses);
        this.lazyFetchRowMappers = collectLazyFetchRowMappers(entityClasses, jdbcTemplate);
    }

    @NotNull
    private Map<Class<?>, EagerFetchRowMapper<?>> collectEagerFetchRowMappers(List<Class<?>> entityClasses) {
        return entityClasses.stream()
                .map(tableDefinitions::get)
                .flatMap(tableDefinition -> tableDefinition.getAssociations().stream())
                .filter(TableAssociationDefinition::isEager)
                .collect(
                        Collectors.toUnmodifiableMap(
                                TableAssociationDefinition::getParentEntityClass,
                                association -> new EagerFetchRowMapper<>(
                                        association.getParentEntityClass(),
                                        tableDefinitions.get(association.getParentEntityClass()),
                                        tableDefinitions.get(association.getAssociatedEntityClass()),
                                        this
                                )
                        )
                );
    }

    @NotNull
    private Map<Class<?>, LazyFetchRowMapper<?>> collectLazyFetchRowMappers(List<Class<?>> entityClasses, JdbcTemplate jdbcTemplate) {
        return entityClasses.stream()
                .map(tableDefinitions::get)
                .flatMap(tableDefinition -> tableDefinition.getAssociations().stream())
                .filter(TableAssociationDefinition::isLazy)
                .collect(
                        Collectors.toUnmodifiableMap(
                                TableAssociationDefinition::getParentEntityClass,
                                association -> new LazyFetchRowMapper<>(
                                        association.getParentEntityClass(),
                                        tableDefinitions.get(association.getParentEntityClass()),
                                        jdbcTemplate,
                                        this
                                )
                        )
                );
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
    private Map<TableAssociationDefinition, EntityCollectionPersister> collectCollectionPersisters(JdbcTemplate jdbcTemplate) {
        return tableDefinitions.values().stream().flatMap(tableDefinition ->
                        tableDefinition.getAssociations().stream()
                )
                .filter(TableAssociationDefinition::isCollection)
                .collect(
                        Collectors.toUnmodifiableMap(
                                association -> association,
                                association -> new EntityCollectionPersister(
                                        tableDefinitions.get(association.getParentEntityClass()),
                                        tableDefinitions.get(association.getAssociatedEntityClass()),
                                        jdbcTemplate
                                )
                        )
                );
    }

    public TableDefinition getTableDefinition(Class<?> clazz) {
        return tableDefinitions.get(clazz);
    }

    public EntityPersister getEntityPersister(Class<?> clazz) {
        return entityPersisters.get(clazz);
    }

    public EntityCollectionPersister getEntityCollectionPersister(TableAssociationDefinition association) {
        return entityCollectionPersisters.get(association);
    }

    @SuppressWarnings("unchecked")
    public <T> RowMapper<T> getRowMapper(Class<T> targetClass) {
        for (var association : tableDefinitions.get(targetClass).getAssociations()) {
            if (association.isEager()) {
                return (RowMapper<T>) eagerFetchRowMappers.get(targetClass);
            }
        }

        return (RowMapper<T>) lazyFetchRowMappers.get(targetClass);
    }
}
