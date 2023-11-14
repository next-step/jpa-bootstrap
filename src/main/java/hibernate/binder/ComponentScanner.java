package hibernate.binder;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ComponentScanner {

    private ComponentScanner() {
    }

    public static List<Class<?>> scan(String basePackage) {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace(".", "/");

        URL resource = Thread.currentThread().getContextClassLoader().getResource(path);
        if (resource == null) {
            return List.of();
        }

        File baseDir = new File(resource.getFile());

        if (baseDir.exists() && baseDir.isDirectory()) {
            for (File file : baseDir.listFiles()) {
                if (file.isDirectory()) {
                    classes.addAll(scan(basePackage + "." + file.getName()));
                } else if (file.getName().endsWith(".class")) {
                    String className = basePackage + "." + file.getName().substring(0, file.getName().length() - 6);
                    classes.add(parseClassByName(className));
                }
            }
        }
        return classes;
    }

    private static Class<?> parseClassByName(String className)  {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("클래스를 찾을 수 없습니다.");
        }
    }
}
