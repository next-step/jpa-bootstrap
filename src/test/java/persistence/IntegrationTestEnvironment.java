package persistence;

import database.DatabaseServer;
import database.H2;
import domain.FixtureEntity.Person;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import persistence.core.*;
import persistence.dialect.h2.H2Dialect;
import persistence.sql.ddl.DdlGenerator;
import persistence.sql.dml.DmlGenerator;
import persistence.util.ReflectionUtils;

import java.sql.SQLException;
import java.util.List;

public abstract class IntegrationTestEnvironment {
    protected static DatabaseServer server;
    protected static PersistenceEnvironment persistenceEnvironment;
    protected static EntityScanner entityScanner;
    protected static EntityMetadataProvider entityMetadataProvider;
    protected static DdlGenerator ddlGenerator;
    protected static DmlGenerator dmlGenerator;
    protected static JdbcTemplate jdbcTemplate;
    protected static EntityMetadata<Person> personEntityMetadata;
    protected static List<Person> people;

    @BeforeAll
    static void integrationBeforeAll() throws SQLException {
        server = new H2();
        server.start();
        jdbcTemplate = new JdbcTemplate(server.getConnection());
        persistenceEnvironment = new PersistenceEnvironment(server, new H2Dialect());
        entityScanner = new EntityScanner(Application.class);
        entityMetadataProvider = EntityMetadataProvider.from(entityScanner);
        final MetaModelFactory metaModelFactory = new MetaModelFactory(entityScanner, persistenceEnvironment);
        ddlGenerator = new DdlGenerator(metaModelFactory.createMetaModel(), persistenceEnvironment.getDialect());
        dmlGenerator = new DmlGenerator(persistenceEnvironment.getDialect());
        personEntityMetadata = entityMetadataProvider.getEntityMetadata(Person.class);
        final String createPersonDdl = ddlGenerator.generateCreateDdl(personEntityMetadata);
        jdbcTemplate.execute(createPersonDdl);
        people = createDummyUsers();
        saveDummyUsers();
    }

    @AfterAll
    static void integrationAfterAll() {
        final String dropDdl = ddlGenerator.generateDropDdl(personEntityMetadata);
        jdbcTemplate.execute(dropDdl);
        server.stop();
    }

    private static List<Person> createDummyUsers() {
        final Person test00 = new Person("test00", 0, "test00@gmail.com");
        final Person test01 = new Person("test01", 10, "test01@gmail.com");
        final Person test02 = new Person("test02", 20, "test02@gmail.com");
        final Person test03 = new Person("test03", 30, "test03@gmail.com");
        return List.of(test00, test01, test02, test03);
    }

    private static void saveDummyUsers() {
        people.forEach(person -> {
            final List<String> columnNames = personEntityMetadata.toInsertableColumnNames();
            final List<Object> values = ReflectionUtils.getFieldValues(person, personEntityMetadata.toInsertableColumnFieldNames());
            jdbcTemplate.execute(dmlGenerator.insert(personEntityMetadata.getTableName(), columnNames, values));
        });
    }


}
