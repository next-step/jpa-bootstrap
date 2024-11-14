package persistence.meta;

import jdbc.JdbcTemplate;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityLoader;
import persistence.entity.EntityPersister;
import persistence.sql.definition.TableAssociationDefinition;

import java.util.Map;
import java.util.stream.Collectors;

public class Metamodel {
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<Class<?>, EntityLoader> entityLoaders;
    private final Map<TableAssociationDefinition, CollectionPersister> collectionPersisters;

    public Metamodel(Metadata metadata,
                     JdbcTemplate jdbcTemplate) {
        this.entityPersisters = collectEntityPersisters(metadata, jdbcTemplate);
        this.entityLoaders = collectEntityLoaders(metadata, jdbcTemplate);
        this.collectionPersisters = collectCollectionPersisters(metadata, jdbcTemplate);
    }

    private Map<Class<?>, EntityPersister> collectEntityPersisters(Metadata metadata,
                                                                   JdbcTemplate jdbcTemplate) {
        return metadata.getEntityClasses().stream().collect(
                Collectors.toUnmodifiableMap(
                        clazz -> clazz,
                        clazz -> new EntityPersister(metadata.findTableDefinition(clazz), jdbcTemplate)
                )
        );
    }

    private Map<Class<?>, EntityLoader> collectEntityLoaders(Metadata metadata, JdbcTemplate jdbcTemplate) {
        return metadata.getEntityClasses().stream().collect(
                Collectors.toUnmodifiableMap(
                        clazz -> clazz,
                        clazz -> new EntityLoader(metadata.findTableDefinition(clazz), jdbcTemplate, this)
                )
        );
    }

    private Map<TableAssociationDefinition, CollectionPersister> collectCollectionPersisters(
            Metadata metadata,
            JdbcTemplate jdbcTemplate) {
        return metadata.findTableDefinitions().stream().flatMap(tableDefinition ->
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

    public EntityPersister findEntityPersister(Class<?> clazz) {
        return entityPersisters.get(clazz);
    }

    public EntityLoader findEntityLoader(Class<?> clazz) {
        return entityLoaders.get(clazz);
    }

    public CollectionPersister findCollectionPersister(TableAssociationDefinition association) {
        return collectionPersisters.get(association);
    }

}
