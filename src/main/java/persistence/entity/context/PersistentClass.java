package persistence.entity.context;

import database.mapping.EntityMetadata;
import database.mapping.EntityMetadataFactory;
import persistence.bootstrap.MetadataImpl;
import persistence.entity.database.CollectionLoader;
import persistence.entity.database.EntityLoader;
import persistence.entity.database.EntityPersister;

public class PersistentClass<T> {

    private final Class<T> mappedClass;
    private final MetadataImpl metadata;
    private final EntityMetadata entityMetadata;

    public PersistentClass(Class<T> mappedClass, MetadataImpl metadata) {
        this.mappedClass = mappedClass;
        this.metadata = metadata;
        this.entityMetadata = EntityMetadataFactory.get(mappedClass);
    }

    public static <T> PersistentClass<T> from(Class<T> clazz, MetadataImpl metadata) {
        return new PersistentClass<>(clazz, metadata);
    }

    public Class<T> getMappedClass() {
        return mappedClass;
    }

    public String getMappedClassName() {
        return getMappedClass().getName();
    }

    public boolean hasAssociation() {
        return entityMetadata.hasAssociation();
    }

    public Long getRowId(Object entity) {
        return entityMetadata.getPrimaryKeyValue(entity);
    }

    public CollectionLoader<T> getCollectionLoader() {
        CollectionLoader<T> collectionLoaderByClass = metadata.getCollectionLoaderByClass(mappedClass);
        if (collectionLoaderByClass == null) {
            throw new RuntimeException("no metadata for class: " + mappedClass.getName());
        }
        return collectionLoaderByClass;
    }

    public EntityLoader<T> getEntityLoader() {
        return metadata.getEntityLoaderByClass(mappedClass);
    }

    public EntityPersister<T> getEntityPersister() {
        return metadata.getEntityPersisterByClass(mappedClass);
    }
}
