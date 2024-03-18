package bootstrap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ComponentScanner {

    public List<Class<?>> scan(String basePackage) throws IOException, ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace(".", "/");
        File baseDir = new File(Thread.currentThread().getContextClassLoader().getResource(path).getFile());

        if (baseDir.exists() && baseDir.isDirectory()) {
            for (File file : baseDir.listFiles()) {
                scanFile(basePackage, file, classes);
            }
        }
        return classes;
    }

    private void scanFile(String basePackage, File file, List<Class<?>> classes) throws IOException, ClassNotFoundException {
        if (file.isDirectory()) {
            classes.addAll(scan(basePackage + "." + file.getName()));
        } else if (file.getName().endsWith(".class")) {
            String className = basePackage + "." + getClassNameFromFile(file);
            classes.add(Class.forName(className));
        }
    }

    private String getClassNameFromFile(File file) {
        String fileName = file.getName();
        return fileName.substring(0, fileName.length() - 6);
    }
}
