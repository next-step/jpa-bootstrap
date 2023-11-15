package persistence.sql.meta;

import jakarta.persistence.Entity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntityMetaScanner {

    private static final String BASE_PACKAGE = "domain";

    public List<Class<?>> scan() throws IOException, ClassNotFoundException {
        return scan(BASE_PACKAGE);
    }

    private List<Class<?>> scan(String packageDir) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = packageDir.replace(".", "/");
        File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());

        if (!baseDir.exists() || !baseDir.isDirectory()) {
            return classes;
        }
        for (File file : baseDir.listFiles()) {
            addClasses(packageDir, file, classes);
        }
        return classes;
    }

    private void addClasses(String packageDir, File file, List<Class<?>> classes) throws IOException, ClassNotFoundException {
        if (file.isDirectory()) {
            classes.addAll(scan(packageDir + "." + file.getName()));
            return;
        }
        if (!file.getName().endsWith(".class")) {
            return;
        }
        String className = packageDir + "." + file.getName().substring(0, file.getName().length() - 6);
        Class<?> addTarget = Class.forName(className);
        if (addTarget.isAnnotationPresent(Entity.class)) {
            classes.add(addTarget);
        }
    }

}
