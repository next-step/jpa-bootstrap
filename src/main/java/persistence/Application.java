package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Department;
import domain.Employee;
import jdbc.JdbcTemplate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.entity.EntityManager;
import persistence.entity.EntityManagerImpl;
import persistence.entity.StatefulPersistenceContext;
import persistence.meta.Metamodel;
import persistence.meta.MetamodelInitializer;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            final MetamodelInitializer metamodelInitializer = new MetamodelInitializer(jdbcTemplate);

            server.start();

            jdbcTemplate.execute("CREATE TABLE department (department_id BIGINT AUTO_INCREMENT, name VARCHAR(255), PRIMARY KEY (department_id));");
            jdbcTemplate.execute("CREATE TABLE employee (id BIGINT AUTO_INCREMENT, name VARCHAR(255), department_id BIGINT, PRIMARY KEY (id), FOREIGN KEY (department_id) REFERENCES department(department_id));");

            final EntityManager em = getEntityManager(metamodelInitializer, jdbcTemplate);

            Department department = new Department("IT");
            Employee employee = new Employee("John Doe");
            department.getEmployees().add(employee);

            em.persist(department);
            em.clear();

            Department saved = em.find(Department.class, 1L);

            logger.info("lazy loading...");
            saved.getEmployees().forEach(emp -> logger.info(emp.getName()));
            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }

    @NotNull
    private static EntityManager getEntityManager(MetamodelInitializer metamodelInitializer, JdbcTemplate jdbcTemplate) {
        Metamodel metamodel = metamodelInitializer.getMetamodel();
        final EntityManager em = new EntityManagerImpl(jdbcTemplate, new StatefulPersistenceContext(), metamodel);
        return em;
    }
}
