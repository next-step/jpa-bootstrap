package hibernate.metamodel;

import hibernate.binder.AnnotationBinder;
import hibernate.entity.meta.EntityClass;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BasicMetaModel {

    private final Map<Class<?>, EntityClass<?>> entityClassMap;

    private BasicMetaModel(final Map<Class<?>, EntityClass<?>> entityClassMap) {
        this.entityClassMap = entityClassMap;
    }

    public static BasicMetaModel createPackageMetaModel(final String packageName) {
        List<Class<?>> classes = AnnotationBinder.parseEntityClasses(packageName);
        Map<Class<?>, EntityClass<?>> entityClassMap = classes
                .stream()
                .collect(Collectors.toMap(clazz -> clazz, EntityClass::new));
        return new BasicMetaModel(entityClassMap);
    }

    public Map<Class<?>, EntityClass<?>> getEntityClassMap() {
        return Collections.unmodifiableMap(entityClassMap);
    }
}
