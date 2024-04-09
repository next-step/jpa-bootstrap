package persistence;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.Proxy.CglibProxyFactory;
import persistence.model.MappingMetaModelImpl;
import persistence.model.MetaModel;
import persistence.sql.JdbcServerTest;
import persistence.sql.TestJdbcServerExtension;
import persistence.sql.dialect.H2Dialect;
import persistence.sql.dml.DefaultDmlQueryBuilder;
import persistence.sql.dml.DmlQueryBuilder;
import persistence.sql.mapping.TableBinder;

@JdbcServerTest
public abstract class EntityMetaDataTestSupport {
    private static final Logger log = LoggerFactory.getLogger(EntityMetaDataTestSupport.class);

    private static final TableBinder tableBinder = new TableBinder();
    private static final DmlQueryBuilder dmlQueryBuilder = new DefaultDmlQueryBuilder(new H2Dialect());
    protected static MetaModel metaModel;

    @BeforeAll
    static void setUpEntityMetaDataTestSupportClass() {
        log.info("set up test class");
        metaModel = new MappingMetaModelImpl(tableBinder, dmlQueryBuilder, TestJdbcServerExtension.getJdbcTemplate(), new CglibProxyFactory());
//        PersistentClassMapping.initialize();
//        PersistentClassMapping.putPersistentClass(PersonV1.class);
//        PersistentClassMapping.putPersistentClass(PersonV2.class);
//        PersistentClassMapping.putPersistentClass(PersonV3.class);
//        PersistentClassMapping.putPersistentClass(Order.class);
//        PersistentClassMapping.putPersistentClass(EagerOrderItem.class);
//        PersistentClassMapping.putPersistentClass(LazyOrderItem.class);
//        PersistentClassMapping.setCollectionPersistentClassBinder();
    }
}
