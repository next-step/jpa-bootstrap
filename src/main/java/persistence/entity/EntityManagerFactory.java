package persistence.entity;

import jdbc.JdbcTemplate;
import persistence.sql.dml.Query;
import utils.ComponentScanner;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityManagerFactory {

    private static EntityManager INSTANCE;

    private static final String ROOT_PACKAGE_NAME = "domain";

    public EntityManagerFactory(JdbcTemplate jdbcTemplate, List<Class<?>> classList) {
        INSTANCE = new EntityManagerImpl(persisterInit(jdbcTemplate, classList));
    }

    public static EntityManager of(Connection connection) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(connection);
        List<Class<?>> classList = ComponentScanner.scan(ROOT_PACKAGE_NAME);

        new EntityManagerFactory(jdbcTemplate, classList);

        return INSTANCE;
    }

    private static Map<String, EntityPersister<?>> persisterInit(JdbcTemplate jdbcTemplate, List<Class<?>> list) {
        Map<String, EntityPersister<?>> map = new HashMap<>();

        list.forEach(aClass -> map.put(aClass.getName(), new EntityPersister<>(jdbcTemplate, aClass, Query.getInstance())));

        return map;
    }

    public static EntityManager get() {
        return INSTANCE;
    }
}
