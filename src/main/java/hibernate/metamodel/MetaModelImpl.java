package hibernate.metamodel;

import hibernate.binder.AnnotationBinder;
import hibernate.entity.meta.EntityClass;

import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class MetaModelImpl implements MetaModel {

    private final Map<Class<?>, EntityClass<?>> entityClassMap;

    private MetaModelImpl(final Map<Class<?>, EntityClass<?>> entityClassMap) {
        this.entityClassMap = entityClassMap;
    }

    public static MetaModel createPackageMetaModel(final String packageName) {
        Map<Class<?>, EntityClass<?>> entityClassMap = AnnotationBinder.parseEntityClasses(packageName)
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, EntityClass::new));
        return new MetaModelImpl(entityClassMap);
    }

    @Override
    public Map<Class<?>, EntityClass<?>> getEntityClasses() {
        return Collections.unmodifiableMap(entityClassMap);
    }

    @Override
    public <T> EntityClass<T> getEntityClass(final Class<T> clazz) {
        if (entityClassMap.containsKey(clazz)) {
            return (EntityClass<T>) entityClassMap.get(clazz);
        }
        throw new IllegalArgumentException("해당 클래스는 엔티티 클래스가 아닙니다.");
    }
}
