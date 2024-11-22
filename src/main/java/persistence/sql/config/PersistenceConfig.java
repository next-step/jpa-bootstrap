package persistence.sql.config;

import boot.MetaModel;
import boot.Metadata;
import database.DatabaseServer;
import database.H2;
import event.DeleteEventListener;
import event.EventListenerGroup;
import event.EventListenerRegistry;
import event.EventType;
import event.LoadEventListener;
import event.SaveOrUpdateEventListener;
import event.impl.DefaultDeleteEventListener;
import event.impl.DefaultEventListenerGroup;
import event.impl.DefaultEventListenerRegistry;
import event.impl.DefaultLoadEventListener;
import event.impl.DefaultSaveOrUpdateEventListener;
import persistence.proxy.ProxyFactory;
import persistence.proxy.impl.JdkProxyFactory;
import persistence.sql.common.util.CamelToSnakeConverter;
import persistence.sql.common.util.NameConverter;
import persistence.sql.context.PersistenceContext;
import persistence.sql.context.impl.DefaultPersistenceContext;
import persistence.sql.ddl.QueryColumnSupplier;
import persistence.sql.ddl.QueryConstraintSupplier;
import persistence.sql.ddl.TableScanner;
import persistence.sql.ddl.impl.AnnotatedTableScanner;
import persistence.sql.ddl.impl.ColumnGeneratedValueSupplier;
import persistence.sql.ddl.impl.ColumnNameSupplier;
import persistence.sql.ddl.impl.ColumnOptionSupplier;
import persistence.sql.ddl.impl.ConstraintPrimaryKeySupplier;
import persistence.sql.ddl.impl.H2ColumnTypeSupplier;
import persistence.sql.ddl.impl.H2Dialect;
import persistence.sql.dml.Database;
import persistence.sql.dml.EntityManagerFactory;
import persistence.sql.dml.impl.DefaultDatabase;
import persistence.sql.dml.impl.DefaultEntityManagerFactory;
import persistence.sql.node.EntityNode;

import java.sql.SQLException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class PersistenceConfig {
    private static final PersistenceConfig INSTANCE = new PersistenceConfig();

    private DatabaseServer databaseServer;
    private PersistenceContext persistenceContext;
    private MetaModel metaModel;
    private Metadata metadata;

    private PersistenceConfig() {
    }

    public static PersistenceConfig getInstance() {
        return INSTANCE;
    }

    public TableScanner tableScanner() {
        return new AnnotatedTableScanner();
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

    public MetaModel metalModel() throws SQLException {
        if (metaModel != null) {
            return metaModel;
        }
        metaModel = MetaModel.newInstance(metadata(), proxyFactory());

        return metaModel;
    }

    public EntityManagerFactory entityManagerFactory() throws SQLException {
        return new DefaultEntityManagerFactory(metaModel(), eventListenerRegistry());
    }

    public MetaModel metaModel() throws SQLException {
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

    private EventListenerRegistry eventListenerRegistry() {
        DefaultEventListenerRegistry registry = new DefaultEventListenerRegistry();
        registry.addEventListenerGroup(EventType.SAVE_OR_UPDATE, saveOrUpdateEventListenerGroup());
        registry.addEventListenerGroup(EventType.DELETE, deleteEventListenerGroup());
        registry.addEventListenerGroup(EventType.LOAD, loadEventListenerGroup());

        return registry;
    }

    private EventListenerGroup<?> loadEventListenerGroup() {
        DefaultEventListenerGroup<LoadEventListener> listeners = new DefaultEventListenerGroup<>(EventType.LOAD);
        listeners.addEventListener(new DefaultLoadEventListener());

        return listeners;
    }

    private EventListenerGroup<?> deleteEventListenerGroup() {
        DefaultEventListenerGroup<DeleteEventListener> listeners = new DefaultEventListenerGroup<>(EventType.DELETE);
        listeners.addEventListener(new DefaultDeleteEventListener());

        return listeners;
    }

    private EventListenerGroup<SaveOrUpdateEventListener> saveOrUpdateEventListenerGroup() {
        DefaultEventListenerGroup<SaveOrUpdateEventListener> saveOrUpdateGroup =
                new DefaultEventListenerGroup<>(EventType.SAVE_OR_UPDATE);
        saveOrUpdateGroup.addEventListener(new DefaultSaveOrUpdateEventListener());

        return saveOrUpdateGroup;
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

    public ProxyFactory proxyFactory() {
        return new JdkProxyFactory();
    }
}
