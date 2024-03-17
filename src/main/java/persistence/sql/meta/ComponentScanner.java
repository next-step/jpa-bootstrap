package persistence.sql.meta;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComponentScanner {

    private ComponentScanner() {
    }

    private static final String CLASS_SUFFIX = ".class";
    private static final String DOT = ".";
    private static final String EMPTY = "";
    private static final String PACKAGE_SEPARATOR = "/";

    public static List<Class<?>> scan(String basePackage) {
        String packagePath = basePackage.replace(DOT, PACKAGE_SEPARATOR);

        URL resource = Thread.currentThread().getContextClassLoader().getResource(packagePath);
        if (resource == null) {
            return new ArrayList<>();
        }

        File baseDirectory = new File(resource.getFile());
        return scanDirectory(basePackage, baseDirectory);
    }

    private static List<Class<?>> scanDirectory(String packageName, File directory) {
        if (!directory.exists() || !directory.isDirectory()) {
            return new ArrayList<>();
        }

        File[] files = directory.listFiles((dir, name) ->
            name.endsWith(CLASS_SUFFIX) || new File(dir, name).isDirectory());

        if (files == null) {
            return new ArrayList<>();
        }

        return Arrays.stream(files)
            .flatMap(file -> processFile(packageName, file))
            .collect(Collectors.toList());
    }

    private static Stream<Class<?>> processFile(String packageName, File file) {
        if (file.isDirectory()) {
            return scanDirectory(packageName + DOT + file.getName(), file).stream();
        }
        return Stream.of(convertToFileClass(packageName, file));

    }

    private static Class<?> convertToFileClass(String packageName, File file) {
        try {
            String className = packageName + DOT + file.getName().replace(CLASS_SUFFIX,  EMPTY);
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
