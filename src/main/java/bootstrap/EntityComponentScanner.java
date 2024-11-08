package bootstrap;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EntityComponentScanner {
    private static final String DOT = ".";
    private static final String CLASS_FILE_EXTENSION = DOT + "class";

    public List<Class<?>> scan(String basePackage) throws ClassNotFoundException {
        final File baseDir = resolveBaseDir(basePackage);

        if (isInvalidDirectory(baseDir)) {
            return new ArrayList<>();
        }

        return Arrays.stream(baseDir.listFiles())
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return handleDirectory(basePackage, file).stream();
                    } else if (isClassFile(file)) {
                        return handleClassFile(basePackage, file).stream();
                    }
                    return Stream.empty();
                }).toList();
    }

    private File resolveBaseDir(String basePackage) {
        final URL resourcePath = getResourcePath(basePackage);
        if (resourcePath == null) {
            return new File("");
        }
        return new File(resourcePath.getFile());
    }

    private URL getResourcePath(String basePackage) {
        String path = basePackage.replace(".", "/");
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    private List<Class<?>> handleClassFile(String basePackage, File file) {
        String className = extractClassName(basePackage, file);
        try {
            Class<?> aClass = Class.forName(className);
            if (isEntity(aClass)) {
                return List.of(aClass);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return new ArrayList<>();
    }

    private List<Class<?>> handleDirectory(String basePackage, File file) {
        try {
            return scan(basePackage + "." + file.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isInvalidDirectory(File file) {
        return !file.exists() || !file.isDirectory() || file.listFiles() == null;
    }

    private String extractClassName(String basePackage, File file) {
        return basePackage + DOT + removeClassFileExtension(file);
    }

    private String removeClassFileExtension(File file) {
        return file.getName().substring(0, file.getName().length() - CLASS_FILE_EXTENSION.length());
    }

    private boolean isClassFile(File file) {
        return file.getName().endsWith(CLASS_FILE_EXTENSION);
    }

    private boolean isEntity(Class<?> aClass) {
        return aClass.isAnnotationPresent(jakarta.persistence.Entity.class);
    }
}
