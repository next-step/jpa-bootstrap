package persistence.entity.proxy;

public interface LazyLoadingHandler {
    Object load(LazyLoadingContext context);
}
