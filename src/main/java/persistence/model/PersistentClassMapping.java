package persistence.model;

import jakarta.persistence.Transient;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class PersistentClassMapping {

    private final Map<String, PersistentClass<?>> persistentClassMap = new HashMap<>();
    private final CollectionPersistentClassBinder collectionPersistentClassBinder = new CollectionPersistentClassBinder();
    private final EntityJoinFieldMapper entityJoinFieldMapper = new EntityJoinFieldMapper();

    public <T> void putPersistentClass(final Class<T> entityClass) {
        final PersistentClass<T> persistentClass = createPersistentClass(entityClass);
        persistentClassMap.putIfAbsent(persistentClass.getEntityName(), persistentClass);
    }

    private <T> PersistentClass<T> createPersistentClass(final Class<T> entityClass) {
        final PersistentClass<T> persistentClass = new PersistentClass<>(entityClass);
        final List<AbstractEntityField> entityFields = extractEntityFields(entityClass);
        persistentClass.addEntityFields(entityFields);

        return persistentClass;
    }

    private <T> List<AbstractEntityField> extractEntityFields(final Class<?> entityClass) {
        return Arrays.stream(entityClass.getDeclaredFields()).filter(this::isColumnField).map(AbstractEntityField::createEntityField).collect(Collectors.toList());
    }

    private boolean isColumnField(final Field field) {
        return !field.isAnnotationPresent(Transient.class);
    }

    private <T> void createCollectionPersistentClass(final PersistentClass<T> persistentClass, final AbstractEntityField entityField) {
        final Field field = entityField.getField();
        final EntityJoinField entityJoinField = (EntityJoinField) entityField;

        final EntityJoinFieldMapping joinFieldMapping = entityJoinFieldMapper.findJoinFieldMapping(field);

        final boolean lazy = joinFieldMapping.isLazy(field);
        entityJoinField.setLazy(lazy);

        final Class<?> entityType = joinFieldMapping.getEntityType(field);
        final PersistentClass<?> joinedPersistentClass = getPersistentClass(entityType.getName());
        final CollectionPersistentClass collectionPersistentClass =
                collectionPersistentClassBinder.getCollectionPersistentClassOrDefault(entityType.getName(), joinFieldMapping.createCollectionPersistentClass(joinedPersistentClass));
        collectionPersistentClass.addAssociation(persistentClass, lazy);
    }

    public void setCollectionPersistentClassBinder() {
        persistentClassMap.values().forEach(persistentClass -> {
            final List<EntityJoinField> joinFields = persistentClass.getFields().stream()
                    .filter(AbstractEntityField::isJoinField)
                    .map(joinField -> (EntityJoinField) joinField)
                    .collect(Collectors.toList());
            joinFields.forEach(field -> createCollectionPersistentClass(persistentClass, field));
        });
    }

    public <T> PersistentClass<T> getPersistentClass(final Class<T> clazz) {
        return (PersistentClass<T>) getPersistentClass(clazz.getName());
    }

    public PersistentClass<?> getPersistentClass(final String entityName) {
        final PersistentClass<?> persistentClass = persistentClassMap.get(entityName);

        if (Objects.isNull(persistentClass)) {
            throw new MetaDataModelMappingException("entity meta data is not initialized : " + entityName);
        }

        return persistentClass;
    }

    public CollectionPersistentClassBinder getCollectionPersistentClassBinder() {
        return collectionPersistentClassBinder;
    }

}
