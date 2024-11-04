package persistence.meta;

import bootstrap.EntityComponentScanner;
import jdbc.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class MetamodelCollector {
    private final Metamodel metamodel;

    public MetamodelCollector(JdbcTemplate jdbcTemplate) {
        Properties properties = new Properties();
        String packageName = loadPackageName(properties);
        List<Class<?>> entityClasses;

        try {
            entityClasses = new EntityComponentScanner().scan(packageName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.metamodel = new Metamodel(entityClasses, jdbcTemplate);
    }

    private String loadPackageName(Properties properties) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Could not load properties file", e);
        }

        return properties.getProperty("entity.package", "domain");
    }

    public Metamodel getMetamodel() {
        return metamodel;
    }

}

