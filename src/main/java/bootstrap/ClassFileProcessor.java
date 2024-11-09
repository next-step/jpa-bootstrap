package bootstrap;

import java.io.File;

public class ClassFileProcessor {
    private static final String DOT = ".";
    private static final String CLASS_FILE_EXTENSION = DOT + "class";

    public Class<?> process(String basePackage, File file) {
        String className = extractClassName(basePackage, file);
        try {
            Class<?> aClass = Class.forName(className);
            if (isEntity(aClass)) {
                return aClass;
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public boolean isClassFile(File file) {
        return file.getName().endsWith(CLASS_FILE_EXTENSION);
    }

    private String extractClassName(String basePackage, File file) {
        return basePackage + DOT + removeClassFileExtension(file);
    }

    private String removeClassFileExtension(File file) {
        return file.getName().substring(0, file.getName().length() - CLASS_FILE_EXTENSION.length());
    }

    private boolean isEntity(Class<?> aClass) {
        return aClass.isAnnotationPresent(jakarta.persistence.Entity.class);
    }
}
