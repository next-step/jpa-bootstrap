package registry;

import jakarta.persistence.Entity;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EntityScanner {

    private static final String CLAZZ_SUFFIX = ".class";
    private static final String PACKAGE_HIERARCHY_FORMAT = "%s.%s";

    public EntityScanner() {
    }

    public List<Class<?>> scan(String basePackage) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace(".", "/");
        File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());

        if (baseDir.exists() && baseDir.isDirectory()) {
            for (File file : baseDir.listFiles()) {
                addEntityClass(basePackage, file, classes);
            }
        }
        return classes;
    }

    private void addEntityClass(String basePackage, File file, List<Class<?>> classes) throws IOException, ClassNotFoundException {
        if (file.isDirectory()) {
            classes.addAll(scan(String.format(PACKAGE_HIERARCHY_FORMAT, basePackage, file.getName())));
        }

        if (isNotClass(file)) {
            return;
        }

        final Class<?> clazz = getClassByName(basePackage, file);
        if (isNotEntityClass(clazz)) {
            return;
        }

        classes.add(clazz);
    }

    private static boolean isClass(File file) {
        return file.getName().endsWith(CLAZZ_SUFFIX);
    }

    private static boolean isNotClass(File file) {
        return !isClass(file);
    }

    private boolean isEntityClass(Class<?> clazz) {
        return clazz.isAnnotationPresent(Entity.class);
    }

    private boolean isNotEntityClass(Class<?> clazz) {
        return !isEntityClass(clazz);
    }

    private static Class<?> getClassByName(String basePackage, File file) throws ClassNotFoundException {
        String className = String.format(PACKAGE_HIERARCHY_FORMAT, basePackage, file.getName().substring(0, file.getName().length() - 6));
        return Class.forName(className);
    }


}
