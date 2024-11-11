package persistence.bootstrap;

import jakarta.persistence.Entity;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityScanner {
    public static final String NOT_EXISTS_PACKAGE_FAILED_MESSAGE = "존재하지 않는 패키지입니다.";
    private static final String PACKAGE_DELIMITER = ".";
    private static final String DIRECTORY_DELIMITER = "/";
    private static final String CLASS_FILE_POSTFIX = ".class";

    private EntityScanner() {
        throw new AssertionError();
    }

    public static List<Class<?>> scan(String basePackage) {
        String path = basePackage.replace(PACKAGE_DELIMITER, DIRECTORY_DELIMITER);
        File baseDir = getBaseDir(path);

        if (baseDir.exists() && baseDir.isDirectory()) {
            return scan(basePackage, baseDir);
        }
        return new ArrayList<>();
    }

    private static File getBaseDir(String path) {
        final URL resource = Thread.currentThread()
                .getContextClassLoader()
                .getResource(path);

        if (Objects.isNull(resource)) {
            throw new IllegalArgumentException(NOT_EXISTS_PACKAGE_FAILED_MESSAGE);
        }

        return new File(resource.getFile());
    }

    private static List<Class<?>> scan(String basePackage, File baseDir) {
        return Arrays.stream(baseDir.listFiles())
                .flatMap(file -> getEntityTypes(basePackage, file).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private static List<Class<?>> getEntityTypes(String basePackage, File file) {
        if (file.isDirectory()) {
            return scan(getSubPackage(basePackage, file), file);
        }

        if (isClassFile(file)) {
            String entityType = getEntityTypeName(basePackage, file);
            return getEntityType(entityType);
        }
        return new ArrayList<>();
    }

    private static String getSubPackage(String basePackage, File file) {
        return basePackage + PACKAGE_DELIMITER + file.getName();
    }

    private static boolean isClassFile(File file) {
        return file.getName().endsWith(CLASS_FILE_POSTFIX);
    }

    private static String getEntityTypeName(String basePackage, File file) {
        return basePackage + PACKAGE_DELIMITER
                + file.getName().substring(0, file.getName().length() - CLASS_FILE_POSTFIX.length());
    }

    private static List<Class<?>> getEntityType(String entityTypeName) {
        Class<?> entityType;
        try {
            entityType = Class.forName(entityTypeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("엔티티 스캔에 실패하였습니다.");
        }

        if (entityType.isAnnotationPresent(Entity.class)) {
            return List.of(entityType);
        }
        return new ArrayList<>();
    }
}
