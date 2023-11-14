package persistence.sql.meta;

import jakarta.persistence.Entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntityMetaScanner {

    public List<Class<?>> scan(String basePackage) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace(".", "/");
        File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());

        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return classes;
        }
        for (File file : baseDir.listFiles()) {
            addClasses(basePackage, file, classes);
        }
        return classes;
    }

    private void addClasses(String basePackage, File file, List<Class<?>> classes) throws IOException, ClassNotFoundException {
        if (file.isDirectory()) {
            classes.addAll(scan(basePackage + "." + file.getName()));
            return;
        }
        if (!file.getName().endsWith(".class")) {
            return;
        }
        String className = basePackage + "." + file.getName().substring(0, file.getName().length() - 6);
        Class<?> addTarget = Class.forName(className);
        if (addTarget.isAnnotationPresent(Entity.class)) {
            classes.add(addTarget);
        }
    }


}
