package bootstrap;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class EntityComponentScanner {
    private final FileSystemExplorer fileSystemExplorer;
    private final ClassFileProcessor classFileProcessor;

    public EntityComponentScanner(FileSystemExplorer fileSystemExplorer,
                                  ClassFileProcessor classFileProcessor) {
        this.fileSystemExplorer = fileSystemExplorer;
        this.classFileProcessor = classFileProcessor;
    }

    public List<Class<?>> scan(String basePackage) throws ClassNotFoundException {
        final File baseDir = fileSystemExplorer.resolveBaseDir(basePackage);

        if (fileSystemExplorer.isInvalidDirectory(baseDir)) {
            return new ArrayList<>();
        }

        return processDirectory(basePackage, baseDir);
    }

    private List<Class<?>> processDirectory(String basePackage, File baseDir) {
        return Arrays.stream(baseDir.listFiles())
                .flatMap(file -> {
                    if (file.isDirectory()) {
                        return handleDirectory(basePackage, file).stream();
                    } else if (classFileProcessor.isClassFile(file)) {
                        return Stream.ofNullable(classFileProcessor.process(basePackage, file));
                    }
                    return Stream.empty();
                }).toList();
    }


    private List<Class<?>> handleDirectory(String basePackage, File file) {
        try {
            return scan(basePackage + "." + file.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
