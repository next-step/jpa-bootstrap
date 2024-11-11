package persistence;

import database.DatabaseServer;
import database.H2;
import domain.Department;
import domain.Employee;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import persistence.meta.Metadata;
import persistence.meta.MetadataImpl;
import persistence.session.EntityManager;
import persistence.session.EntityManagerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) throws SQLException {
        logger.info("Starting application...");
        final DatabaseServer server = new H2();

        try {
            server.start();
            Metadata metadata = new MetadataImpl(server);

            doInJpa(metadata::buildEntityManagerFactory,
                    em -> {
                        Department department = new Department("IT");
                        Employee employee = new Employee("John Doe");
                        Employee employee2 = new Employee("Jane Doe");

                        department.getEmployees().addAll(List.of(employee, employee2));

                        em.persist(department);
                        em.clear();

                        Department saved = em.find(Department.class, 1L);

                        logger.info("before lazy loading...");
                        saved.getEmployees().forEach(emp -> logger.info(emp.getName()));

                        return saved;
                    }
            );

        } catch (Exception e) {
            logger.error("Error occurred", e);
        } finally {
            server.stop();
            logger.info("Application finished");
        }
    }

    private static <T> T doInJpa(Supplier<EntityManagerFactory> factorySupplier,
                                 Function<EntityManager, T> function) {

        try (EntityManagerFactory emf = factorySupplier.get()) {
            try (EntityManager em = emf.openSession()) {
                return function.apply(em);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
