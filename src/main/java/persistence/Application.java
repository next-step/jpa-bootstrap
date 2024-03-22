package persistence;

import database.DatabaseServer;
import database.H2;
import database.dialect.MySQLDialect;
import entity.Department;
import entity.Employee;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.bootstrap.Initializer;
import persistence.entity.EntityManager;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());

            Initializer initializer = new Initializer("entity", jdbcTemplate, MySQLDialect.getInstance());
            initializer.bootUp();
            initializer.createTables();

            EntityManager entityManager = initializer.newEntityManager();

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
