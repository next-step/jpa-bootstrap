package persistence.entity.proxy;

import persistence.entity.LazyLoader;
import persistence.meta.EntityTable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class LazyLoadingHandler implements InvocationHandler {
    private static final String LAZY_LOADING_FAILED_MESSAGE = "Lazy 로딩에 실패하였습니다.";

    private final Object parentEntity;
    private final LazyLoader lazyLoader;
    private List<?> collection;
    private boolean isLoaded = false;

    public LazyLoadingHandler(Object parentEntity, LazyLoader lazyLoader) {
        this.parentEntity = parentEntity;
        this.lazyLoader = lazyLoader;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        if (!isLoaded) {
            collection = lazyLoader.load();
            setLoadedCollection();
            isLoaded = true;
        }

        try {
            return method.invoke(collection, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException(LAZY_LOADING_FAILED_MESSAGE, e);
        }
    }

    private void setLoadedCollection() {
        final EntityTable entityTable = new EntityTable(parentEntity.getClass());
        entityTable.setValue(parentEntity);

        final Field associationField = entityTable.getAssociationField();

        try {
            associationField.setAccessible(true);
            associationField.set(parentEntity, collection);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(LAZY_LOADING_FAILED_MESSAGE, e);
        }
    }
}
