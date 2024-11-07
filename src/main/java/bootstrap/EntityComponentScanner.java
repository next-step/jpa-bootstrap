package bootstrap;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class EntityComponentScanner {
    private static final String DOT = ".";
    private static final String CLASS_FILE_EXTENSION = DOT + "class";

    public List<Class<?>> scan(String basePackage) throws ClassNotFoundException {
        final URL resourcePath = getResourcePath(basePackage);
        if (resourcePath == null) {
            return new ArrayList<>();
        }

        final File baseDir = new File(resourcePath.getFile());
        if (isInvalidDirectory(baseDir)) {
            return new ArrayList<>();
        }

        return Arrays.stream(Objects.requireNonNull(baseDir.listFiles()))
                .reduce(new ArrayList<>(), (classes, file) -> {
                    if (file.isDirectory()) {
                        handleDirectory(basePackage, classes, file);
                    }

                    if (isClassFile(file)) {
                        handleClassFile(basePackage, classes, file);
                    }

                    return classes;
                }, (origin, newClasses) -> {
                    origin.addAll(newClasses);
                    return origin;
                });
    }

    private URL getResourcePath(String basePackage) {
        String path = basePackage.replace(".", "/");
        return Thread.currentThread().getContextClassLoader().getResource(path);
    }

    private void handleClassFile(String basePackage, ArrayList<Class<?>> classes, File file) {
        String className = extractClassName(basePackage, file);
        try {
            Class<?> aClass = Class.forName(className);
            if (isEntity(aClass)) {
                classes.add(aClass);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleDirectory(String basePackage, ArrayList<Class<?>> classes, File file) {
        try {
            classes.addAll(scan(basePackage + "." + file.getName()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isInvalidDirectory(File file) {
        return !file.exists() || !file.isDirectory();
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
