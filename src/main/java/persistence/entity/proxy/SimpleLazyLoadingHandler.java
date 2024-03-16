package persistence.entity.proxy;

public class SimpleLazyLoadingHandler implements LazyLoadingHandler {
    @Override
    public Object load(LazyLoadingContext context) {
        return context.loading();
    }
}
