package persistence.entity.proxy;

import persistence.entity.LazyLoader;

import java.lang.reflect.Proxy;
import java.util.List;

public class ProxyFactory {
    public <T> List<T> createProxy(Object entity, LazyLoader<T> lazyLoader) {
        return (List<T>) Proxy.newProxyInstance(
                List.class.getClassLoader(),
                new Class[]{List.class},
                new LazyLoadingHandler<>(entity, lazyLoader));
    }
}
