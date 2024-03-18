package persistence.entity.proxy;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.LazyLoader;

public class LazyLoadingProxyFactory {

    private LazyLoadingProxyFactory() {
    }

    public static Object createProxy(LazyLoadingContext context) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(context.getLazyLoadClass());
        enhancer.setCallback((LazyLoader) () -> new SimpleLazyLoadingHandler().load(context));
        return enhancer.create();
    }
}
