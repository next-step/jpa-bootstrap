package testsupport;

import app.entity.Person5;
import database.H2;
import database.dialect.Dialect;
import database.dialect.MySQLDialect;
import jdbc.JdbcTemplate;
import net.sf.cglib.proxy.Enhancer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import persistence.bootstrap.EntityManagerFactory;
import persistence.bootstrap.Initializer;
import persistence.entity.database.EntityPersister;
import persistence.entitymanager.AbstractEntityManager;
import persistence.entitymanager.EntityManager;
import persistence.entitymanager.SessionContract;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

abstract public class H2DatabaseTest {
    protected static final Dialect dialect = MySQLDialect.getInstance();

    protected static H2 server;
    protected Connection connection;
    protected JdbcTemplate jdbcTemplate;
    protected List<String> executedQueries;
    protected EntityManagerFactory entityManagerFactory;

    @BeforeAll
    static void startServer() throws SQLException {
        server = new H2();
        server.start();
    }

    @BeforeEach
    void initJdbcTemplate() throws SQLException {
        connection = server.getConnection();
        executedQueries = new ArrayList<>();
        jdbcTemplate = createJdbcTemplateProxy(executedQueries);
        initializeEntityManagerFactory();
        createTable();
    }

    private JdbcTemplate createJdbcTemplateProxy(List<String> executedQueries) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(JdbcTemplate.class);
        enhancer.setCallback(new LoggingJdbcMethodInterceptor(executedQueries));
        return (JdbcTemplate) enhancer.create(new Class<?>[]{Connection.class}, new Object[]{connection});
    }

    private void createTable() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        jdbcTemplate.execute("DROP TABLE users IF EXISTS");

        SessionContract sessionContract = (SessionContract) entityManagerFactory.openSession();
        EntityPersister<Person5> entityPersister = sessionContract.getEntityPersister(Person5.class);
        entityPersister.dropTable(true);
        entityPersister.createTable();
        executedQueries.clear();
    }

    private void initializeEntityManagerFactory() {
        Initializer initializer = new Initializer("app.entity", jdbcTemplate, dialect);
        initializer.initialize();
        this.entityManagerFactory = initializer.createEntityManagerFactory();
    }

    @AfterAll
    static void shutdownServer() {
        server.stop();
    }
}
