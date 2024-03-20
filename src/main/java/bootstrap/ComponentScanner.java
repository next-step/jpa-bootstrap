package bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComponentScanner {

    private ComponentScanner() {
    }

    public static List<Class<?>> scan(String basePackage) {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace(".", "/");
        File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());
        scanDirectory(basePackage, baseDir, classes);
        return classes;
    }

    private static void scanDirectory(String basePackage, File baseDir, List<Class<?>> classes) {
        if (isNotDirectory(baseDir)) {
            return;
        }
        for (File file : baseDir.listFiles()) {
            scanFile(basePackage, file, classes);
        }
    }

    private static boolean isNotDirectory(File directory) {
        return !directory.exists() || !directory.isDirectory();
    }

    private static void scanFile(String basePackage, File file, List<Class<?>> classes) {
        if (file.isDirectory()) {
            scanDirectory(basePackage + "." + file.getName(), file, classes);
        } else if (file.getName().endsWith(".class")) {
            addClass(basePackage, file, classes);
        }
    }

    private static void addClass(String basePackage, File file, List<Class<?>> classes) {
        String className = basePackage + "." + getClassNameFromFile(file);
        try {
            classes.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("[ERROR] 클래스를 찾지 못했습니다.", e);
        }
    }

    private static String getClassNameFromFile(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.length() - 6);
    }
}
