package persistence.model.scan;

import jakarta.persistence.Entity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class EntityScanner {
    public static List<Class<?>> scan(final String basePackage) {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace(".", "/");
        File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());

        if (baseDir.exists() && baseDir.isDirectory()) {
            for (File file : baseDir.listFiles()) {
                if (file.isDirectory()) {
                    classes.addAll(scan(basePackage + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = basePackage + "." + file.getName().substring(0, file.getName().length() - 6);

                    try {
                        final Class<?> clazz = Class.forName(className);
                        if (clazz.isAnnotationPresent(Entity.class)) {
                            classes.add(clazz);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }

        return classes;
    }
}
