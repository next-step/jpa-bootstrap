package persistence.meta;

import bootstrap.ClassFileProcessor;
import bootstrap.EntityComponentScanner;
import bootstrap.FileSystemExplorer;
import jdbc.JdbcTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class MetamodelInitializer {
    private final Metamodel metamodel;

    public MetamodelInitializer(JdbcTemplate jdbcTemplate) {
        final Properties properties = new Properties();
        final String packageName = loadPackageName(properties);
        final List<Class<?>> entityClasses = scanEntityClasses(packageName);

        this.metamodel = new Metamodel(entityClasses, jdbcTemplate);
    }

    private static List<Class<?>> scanEntityClasses(String packageName) {
        List<Class<?>> entityClasses;
        try {
            entityClasses = new EntityComponentScanner(
                    new FileSystemExplorer(),
                    new ClassFileProcessor()
            ).scan(packageName);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return entityClasses;
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

