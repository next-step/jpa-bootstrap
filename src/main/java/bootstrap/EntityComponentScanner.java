package bootstrap;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EntityComponentScanner {

    public List<Class<?>> scan(String basePackage) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace(".", "/");
        URL resourcePath = Thread.currentThread().getContextClassLoader().getResource(path);

        if (resourcePath == null) {
            return classes;
        }

        File baseDir = new File(resourcePath.getFile());

        if (!baseDir.exists()) {
            return classes;
        }

        if (!baseDir.isDirectory()) {
            return classes;
        }

        for (File file : baseDir.listFiles()) {
            if (isDirectory(file)) {
                classes.addAll(scan(basePackage + "." + file.getName()));
                continue;
            }

            if (isClassFile(file)) {
                String className = extractClassName(basePackage, file);
                Class<?> aClass = Class.forName(className);
                if (isEntity(aClass)) {
                    classes.add(aClass);
                }
            }
        }

        return classes;
    }

    private boolean isDirectory(File file) {
        return file.isDirectory();
    }

    private String extractClassName(String basePackage, File file) {
        return basePackage + "." + file.getName().substring(0, file.getName().length() - 6);
    }

    private boolean isClassFile(File file) {
        return file.getName().endsWith(".class");
    }

    private boolean isEntity(Class<?> aClass) {
        return aClass.isAnnotationPresent(jakarta.persistence.Entity.class);
    }
}
