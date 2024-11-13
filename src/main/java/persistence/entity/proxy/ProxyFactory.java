package persistence.entity.proxy;

import java.lang.reflect.Proxy;
import java.util.List;

public class ProxyFactory {
    public List<?> createProxy(Object parentEntity, LazyLoader lazyLoader) {
        return (List<?>) Proxy.newProxyInstance(
                List.class.getClassLoader(),
                new Class[]{List.class},
                new LazyLoadingHandler(parentEntity, lazyLoader));
    }
}
