package persistence.meta;

import jdbc.JdbcTemplate;
import persistence.entity.CollectionPersister;
import persistence.entity.EntityPersister;
import persistence.sql.definition.TableAssociationDefinition;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Metamodel {
    private final Map<Class<?>, EntityPersister> entityPersisters;
    private final Map<TableAssociationDefinition, CollectionPersister> collectionPersisters;

    public Metamodel(Metadata metadata,
                     JdbcTemplate jdbcTemplate) {
        this.entityPersisters = collectEntityPersisters(metadata, jdbcTemplate);
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

    public CollectionPersister findCollectionPersister(TableAssociationDefinition association) {
        return collectionPersisters.get(association);
    }

    public List<TableAssociationDefinition> resolveEagerAssociation(Class<?> entityClass) {
        return entityPersisters.get(entityClass).getAssociations()
                .stream().filter(TableAssociationDefinition::isEager)
                .collect(Collectors.toList());
    }

}
