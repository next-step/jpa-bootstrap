package persistence.sql.meta;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMetaScanner {

    private static final String BASE_PACKAGE = "domain";

    private final MetaScanFilterStrategy filterStrategy;

    public EntityMetaScanner(MetaScanFilterStrategy filterStrategy) {
        this.filterStrategy = filterStrategy;
    }

    public List<EntityMeta> scan() {
        return scan(BASE_PACKAGE).stream()
                .map(EntityMeta::of)
                .collect(Collectors.toList());
    }

    private List<Class<?>> scan(String packageDir) {
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

    private void addClasses(String packageDir, File file, List<Class<?>> classes) {
        if (file.isDirectory()) {
            classes.addAll(scan(packageDir + "." + file.getName()));
            return;
        }
        if (!file.getName().endsWith(".class")) {
            return;
        }
        String className = packageDir + "." + file.getName().substring(0, file.getName().length() - 6);
        try {
            Class<?> addTarget = Class.forName(className);
            if (filterStrategy.match(addTarget)) {
                classes.add(addTarget);
            }
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("엔티티 클래스가 존재하지 않습니다.");
        }
    }

}
