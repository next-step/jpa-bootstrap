package hibernate.binder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ComponentScanner {

    private ComponentScanner() {
    }

    public static List<Class<?>> scan(String basePackage) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace(".", "/");
        File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());

        if (baseDir.exists() && baseDir.isDirectory()) {
            for (File file : baseDir.listFiles()) {
                if (file.isDirectory()) {
                    classes.addAll(scan(basePackage + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = basePackage + "." + file.getName().substring(0, file.getName().length() - 6);
                    classes.add(Class.forName(className));
                }
            }
        }
        return classes;
    }
}
