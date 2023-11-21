package persistence.testFixtures;

import java.util.Set;
import persistence.entity.EntityManagerFactory;
import persistence.entity.EntityScanner;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;

public class EntityManagerFactoryFixture {

    public static EntityManagerFactory getEntityManagerFactory() {
        EntityScanner scanner = new EntityScanner();
        final Set<Class<?>> scan = scanner.scan("persistence.testFixtures");
        AnnotationBinder annotationBinder = new AnnotationBinder(scan, new FakeDialect());
        return EntityManagerFactory.create(annotationBinder.getMetaModel());
    }


}
