package persistence.bootstrap;

import persistence.bootstrap.binder.EntityBinder;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ComponentScanner {
    public static final String NOT_EXISTS_PACKAGE_FAILED_MESSAGE = "존재하지 않는 패키지입니다.";
    private static final String ENTITY_SCAN_FAILD_MESSAGE = "엔티티 스캔에 실패하였습니다.";
    private static final String PACKAGE_DELIMITER = ".";
    private static final String DIRECTORY_DELIMITER = "/";
    private static final String CLASS_FILE_POSTFIX = ".class";

    private final EntityBinder entityBinder;

    public ComponentScanner(String... basePackages) {
        final List<Class<?>> classes = componentScan(basePackages);
        this.entityBinder = new EntityBinder(classes);
    }

    public List<Class<?>> getEntityTypes() {
        return entityBinder.getEntityTypes();
    }

    private List<Class<?>> componentScan(String[] basePackages) {
        return Arrays.stream(basePackages)
                .flatMap(basePackage -> scan(basePackage).stream())
                .toList();
    }

    private List<Class<?>> scan(String basePackage) {
        String path = basePackage.replace(PACKAGE_DELIMITER, DIRECTORY_DELIMITER);
        File baseDir = getBaseDir(path);

        if (baseDir.exists() && baseDir.isDirectory()) {
            return scan(basePackage, baseDir);
        }
        return new ArrayList<>();
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

    private List<Class<?>> scan(String basePackage, File baseDir) {
        return Arrays.stream(baseDir.listFiles())
                .flatMap(file -> getEntityTypes(basePackage, file).stream())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<Class<?>> getEntityTypes(String basePackage, File file) {
        if (file.isDirectory()) {
            return scan(getSubPackage(basePackage, file), file);
        }

        if (isClassFile(file)) {
            String typeName = getTypeName(basePackage, file);
            return getClass(typeName);
        }
        return new ArrayList<>();
    }

    private String getSubPackage(String basePackage, File file) {
        return basePackage + PACKAGE_DELIMITER + file.getName();
    }

    private boolean isClassFile(File file) {
        return file.getName().endsWith(CLASS_FILE_POSTFIX);
    }

    private String getTypeName(String basePackage, File file) {
        return basePackage + PACKAGE_DELIMITER
                + file.getName().substring(0, file.getName().length() - CLASS_FILE_POSTFIX.length());
    }

    private List<Class<?>> getClass(String entityTypeName) {
        try {
            final Class<?> clazz = Class.forName(entityTypeName);
            return List.of(clazz);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(ENTITY_SCAN_FAILD_MESSAGE);
        }
    }
}
