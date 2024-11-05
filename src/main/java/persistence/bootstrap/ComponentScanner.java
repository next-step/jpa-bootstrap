package persistence.bootstrap;

import jakarta.persistence.Entity;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ComponentScanner {
    public static final String NOT_EXISTS_PACKAGE_FAILED_MESSAGE = "존재하지 않는 패키지입니다.";
    private static final String PACKAGE_DELIMITER = ".";
    private static final String DIRECTORY_DELIMITER = "/";
    private static final String CLASS_FILE_POSTFIX = ".class";

    public List<Class<?>> scan(String basePackage) throws IOException, ClassNotFoundException {
        List<Class<?>> entities = new ArrayList<>();
        String path = basePackage.replace(PACKAGE_DELIMITER, DIRECTORY_DELIMITER);
        File baseDir = getBaseDir(path);

        if (baseDir.exists() && baseDir.isDirectory()) {
            scan(basePackage, baseDir, entities);
        }
        return entities;
    }

    private void scan(String basePackage, File baseDir, List<Class<?>> entities) throws IOException, ClassNotFoundException {
        for (File file : baseDir.listFiles()) {
            if (file.isDirectory()) {
                entities.addAll(scan(basePackage + PACKAGE_DELIMITER + file.getName()));
                continue;
            }

            if (isClassFile(file)) {
                String entityType = basePackage + PACKAGE_DELIMITER + file.getName().substring(0, file.getName().length() - CLASS_FILE_POSTFIX.length());
                addEntity(entityType, entities);
            }
        }
    }

    private File getBaseDir(String path) {
        final URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource(path);

        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException(NOT_EXISTS_PACKAGE_FAILED_MESSAGE);
        }

        return new File(resource.getFile());
    }

    private boolean isClassFile(File file) {
        return file.getName().endsWith(CLASS_FILE_POSTFIX);
    }

    private void addEntity(String entityType, List<Class<?>> entities) throws ClassNotFoundException {
        final Class<?> entityClass = Class.forName(entityType);
        if (entityClass.isAnnotationPresent(Entity.class)) {
            entities.add(entityClass);
        }
    }
}
