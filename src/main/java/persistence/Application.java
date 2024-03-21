package persistence;

import database.DatabaseServer;
import database.H2;
import database.dialect.MySQLDialect;
import entity.Person;
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
            EntityManager entityManager = initializer.newEntityManager();

            System.out.println(entityManager.find(Person.class, 1L));

            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
}
