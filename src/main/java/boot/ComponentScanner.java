package boot;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ComponentScanner {
    public List<Class<?>> scan(String basePackage) throws ClassNotFoundException, URISyntaxException {
        List<Class<?>> classes = new ArrayList<>();
        String path = basePackage.replace('.', '/');
        URI uri = Thread.currentThread().getContextClassLoader().getResource(path).toURI();
        File baseDir = new File(uri);

        if (baseDir.exists() && baseDir.isDirectory()) {
            processDirectory(baseDir, basePackage, classes);
        }
        return classes;
    }

    private void processDirectory(File directory, String packageName, List<Class<?>> classes) throws ClassNotFoundException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    processDirectory(file, packageName + "." + file.getName(), classes);
                } else if (file.getName().endsWith(".class")) {
                    processClassFile(file, packageName, classes);
                }
            }
        }
    }

    private void processClassFile(File classFile, String packageName, List<Class<?>> classes) throws ClassNotFoundException {
        String className = packageName + '.' + classFile.getName().substring(0, classFile.getName().length() - 6);
        classes.add(Class.forName(className));
    }
}
