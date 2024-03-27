package persistence;

import app.entity.Department;
import app.entity.Employee;
import database.DatabaseServer;
import database.H2;
import database.dialect.MySQLDialect;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.bootstrap.EntityManagerFactory;
import persistence.bootstrap.Initializer;
import persistence.entitymanager.EntityManager;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

            Initializer initializer = new Initializer("app.entity", jdbcTemplate, MySQLDialect.getInstance());
            initializer.initialize();
            initializer.createTables();
            EntityManagerFactory entityManagerFactory = initializer.createEntityManagerFactory();
            EntityManager entityManager = entityManagerFactory.openSession();

            entityManager.persist(new Department("A팀"));
            entityManager.persist(new Employee("김선생", 1L));
            entityManager.persist(new Employee("이선생", 1L));

            System.out.println(entityManager.find(Department.class, 1L));
            // Department{id=1, name='A팀', employees=[Employee{id=1, name='김선생', departmentId=1}, Employee{id=2, name='이선생', departmentId=1}]}

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
}
