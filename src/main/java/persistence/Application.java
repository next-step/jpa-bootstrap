package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Department;
import domain.Employee;
import jdbc.JdbcTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.session.EntityManager;
import persistence.session.EntityManagerFactory;
import persistence.session.EntityManagerFactoryImpl;
import persistence.session.ThreadLocalCurrentSessionContext;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        logger.info("Starting application...");
        try {
            final DatabaseServer server = new H2();
            server.start();

            final JdbcTemplate jdbcTemplate = new JdbcTemplate(server.getConnection());
            final EntityManagerFactory emf = new EntityManagerFactoryImpl(new ThreadLocalCurrentSessionContext(), server);


            jdbcTemplate.execute("CREATE TABLE department (department_id BIGINT AUTO_INCREMENT, name VARCHAR(255), PRIMARY KEY (department_id));");
            jdbcTemplate.execute("CREATE TABLE employee (id BIGINT AUTO_INCREMENT, name VARCHAR(255), department_id BIGINT, PRIMARY KEY (id), FOREIGN KEY (department_id) REFERENCES department(department_id));");

            final EntityManager em = emf.openSession();

            Department department = new Department("IT");
            Employee employee = new Employee("John Doe");
            Employee employee2 = new Employee("Jane Doe");
            department.getEmployees().add(employee);
            department.getEmployees().add(employee2);

            em.persist(department);
            em.clear();

            Department saved = em.find(Department.class, 1L);

            saved.getEmployees().forEach(emp -> logger.info(emp.getName()));
            server.stop();
        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            logger.info("Application finished");
        }
    }
}
