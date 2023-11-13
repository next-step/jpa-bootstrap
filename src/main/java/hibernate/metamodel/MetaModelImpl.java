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
                .collect(Collectors.toMap(clazz -> clazz, EntityClass::getInstance));
        return new MetaModelImpl(entityClassMap);
    }

    @Override
    public Map<Class<?>, EntityClass<?>> getEntityClasses() {
        return Collections.unmodifiableMap(entityClassMap);
    }
}
