package bootstrap;

import java.util.List;

public interface Binder {

    List<Class<?>> bind(String basePackage);
}
