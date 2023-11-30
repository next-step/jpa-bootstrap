package persistence.testFixtures;

import persistence.entity.ClassPackageScanner;
import persistence.entity.EntityManagerFactory;
import persistence.entity.ThreadLocalSessionContext;
import persistence.entity.binder.AnnotationBinder;
import persistence.fake.FakeDialect;
import persistence.meta.MetaModel;
import persistence.sql.QueryGenerator;

public class EntityManagerFactoryFixture {

    public static EntityManagerFactory getEntityManagerFactory() {
        MetaModel metaModel = AnnotationBinder.bindMetaModel(new ClassPackageScanner("persistence.testFixtures"));
        return new EntityManagerFactory(metaModel, QueryGenerator.of(new FakeDialect()), new ThreadLocalSessionContext());
    }
}
