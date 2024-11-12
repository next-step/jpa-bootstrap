package persistence.config;

import boot.MetaModel;
import boot.Metadata;
import database.DatabaseServer;
import database.H2;
import persistence.proxy.ProxyFactory;
import persistence.proxy.impl.JdkProxyFactory;
import persistence.sql.common.util.CamelToSnakeConverter;
import persistence.sql.common.util.NameConverter;
import persistence.sql.context.PersistenceContext;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.ddl.JoinTargetScanner;
import persistence.sql.ddl.QueryColumnSupplier;
import persistence.sql.ddl.QueryConstraintSupplier;
import persistence.sql.ddl.TableScanner;
import persistence.sql.ddl.impl.AnnotatedJoinTargetScanner;
import persistence.sql.ddl.impl.AnnotatedTableScanner;
import persistence.sql.ddl.impl.ColumnGeneratedValueSupplier;
import persistence.sql.ddl.impl.ColumnNameSupplier;
import persistence.sql.ddl.impl.ColumnOptionSupplier;
import persistence.sql.ddl.impl.ConstraintPrimaryKeySupplier;
import persistence.sql.ddl.impl.H2ColumnTypeSupplier;
import persistence.sql.ddl.impl.H2Dialect;
import persistence.sql.dml.Database;
import persistence.sql.dml.EntityManager;
import persistence.sql.dml.impl.DefaultDatabase;
import persistence.sql.dml.impl.DefaultEntityManager;
import persistence.sql.fixture.TestPerson;
import persistence.sql.fixture.TestPersonFakeRowMapper;
import persistence.sql.node.EntityNode;
import sample.application.RowMapperFactory;

import java.sql.SQLException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class TestPersistenceConfig {
    private static final TestPersistenceConfig INSTANCE = new TestPersistenceConfig();

    private DatabaseServer databaseServer;
    private PersistenceContext persistenceContext;
    private MetaModel metaModel;
    private Metadata metadata;

    private TestPersistenceConfig() {
    }

    public static TestPersistenceConfig getInstance() {
        return INSTANCE;
    }

    public TableScanner tableScanner() {
        return new AnnotatedTableScanner();
    }

    public JoinTargetScanner joinTargetScanner() {
        return new AnnotatedJoinTargetScanner();
    }

    public NameConverter nameConverter() {
        return CamelToSnakeConverter.getInstance();
    }

    public SortedSet<QueryColumnSupplier> columnQuerySuppliers() {
        SortedSet<QueryColumnSupplier> suppliers = new TreeSet<>();

        suppliers.add(new ColumnNameSupplier((short) 1, nameConverter()));
        suppliers.add(new H2ColumnTypeSupplier((short) 2, H2Dialect.create()));
        suppliers.add(new ColumnGeneratedValueSupplier((short) 3));
        suppliers.add(new ColumnOptionSupplier((short) 4));

        return suppliers;
    }

    private SortedSet<QueryConstraintSupplier> constraintQuerySuppliers() {
        SortedSet<QueryConstraintSupplier> suppliers = new TreeSet<>();

        suppliers.add(new ConstraintPrimaryKeySupplier((short) 1, nameConverter()));

        return suppliers;
    }

    public EntityManager entityManager() throws SQLException {
        return new DefaultEntityManager(persistenceContext(), metalModel());
    }

    public MetaModel metalModel() throws SQLException {
        if (metaModel != null) {
            return metaModel;
        }
        metaModel = MetaModel.newInstance(metadata(), proxyFactory());

        return metaModel;
    }

    public Metadata metadata() throws SQLException {
        if (metadata != null) {
            return metadata;
        }
        Set<EntityNode<?>> nodes = tableScanner().scan("persistence.sql.fixture");
        metadata = Metadata.create(nodes, database());
        return metadata;
    }

    public PersistenceContext persistenceContext() throws SQLException {
        if (persistenceContext == null) {
            persistenceContext = new DefaultPersistenceContext();
        }
        return persistenceContext;
    }

    public Database database() throws SQLException {
        return new DefaultDatabase(databaseServer());
    }

    public DatabaseServer databaseServer() throws SQLException {
        if (databaseServer == null) {
            databaseServer = new H2();
            return databaseServer;
        }
        return databaseServer;
    }

    public RowMapperFactory rowMapperFactory() {
        return new RowMapperFactory()
                .addRowMapper(TestPerson.class, new TestPersonFakeRowMapper());
    }

    public ProxyFactory proxyFactory() {
        return new JdkProxyFactory();
    }
}
