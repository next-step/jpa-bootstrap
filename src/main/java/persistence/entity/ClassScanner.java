package persistence.entity;

import java.util.Set;

public interface ClassScanner {
    Set<Class<?>> scan();
}
