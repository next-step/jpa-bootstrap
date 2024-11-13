package persistence.entity.proxy;

import java.lang.reflect.Proxy;
import java.util.List;

public class ProxyFactory {
    private static class ProxyFactoryHolder {
        private static final ProxyFactory INSTANCE = new ProxyFactory();
    }

    public static ProxyFactory getInstance() {
        return ProxyFactoryHolder.INSTANCE;
    }

    private ProxyFactory() {
    }

    public List<?> createProxy(Object parentEntity, LazyLoader lazyLoader) {
        return (List<?>) Proxy.newProxyInstance(
                List.class.getClassLoader(),
                new Class[]{List.class},
                new LazyLoadingHandler(parentEntity, lazyLoader));
    }
}
