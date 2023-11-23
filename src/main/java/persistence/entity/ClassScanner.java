package persistence.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

public final class ClassScanner {
    private ClassScanner() {}

    public static Set<Class<?>> scan(String packageName) {
        final InputStream packageNameWithResource = getPackageNameWithResource(packageName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(packageNameWithResource))) {
            return reader.lines()
                    .filter(ClassScanner::isClassFile)
                    .map(line -> getClass(line, packageName))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException("클래스를 읽을 수 없습니다.", e);
        }
    }

    private static InputStream getPackageNameWithResource(String packageName) {
        final String path = packageName.replaceAll("[.]", "/");
        final InputStream resourceAsStream = ClassLoader.getSystemClassLoader().getResourceAsStream(path);
        if (resourceAsStream == null) {
            throw new IllegalArgumentException("해당 패키지가 존재 하지 않습니다");
        }
        return resourceAsStream;

    }

    private static Class<?> getClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("클래스를 찾을 수 없습니다.");
        }
    }

    private static boolean isClassFile(String line) {
        return line.endsWith(".class");
    }


}
