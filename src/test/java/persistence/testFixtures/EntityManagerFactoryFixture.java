package persistence.testFixtures;

import java.util.Set;
import persistence.entity.ClassScanner;
import persistence.entity.EntityClassFilter;
import persistence.entity.EntityManagerFactory;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.meta.MetaModel;

public class EntityManagerFactoryFixture {

    public static EntityManagerFactory getEntityManagerFactory() {
        final Set<Class<?>> entityClass = EntityClassFilter.entityFilter(ClassScanner.scan("persistence.testFixtures"));

        MetaModel metaModel = AnnotationBinder.bindMetaModel(entityClass, new FakeDialect());

        return EntityManagerFactory.create(metaModel);
    }


}
