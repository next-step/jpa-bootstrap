package persistence.entity.proxy;

import jakarta.persistence.OneToMany;
import persistence.entity.LazyLoader;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
            collection = lazyLoader.load(parentEntity);
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
        final Field associationField = getAssociationField(parentEntity.getClass());
        try {
            associationField.setAccessible(true);
            associationField.set(parentEntity, collection);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(LAZY_LOADING_FAILED_MESSAGE, e);
        }
    }

    private Field getAssociationField(Class<?> entityType) {
        return Arrays.stream(entityType.getDeclaredFields())
                .filter(this::isOneToMany)
                .findFirst()
                .orElseThrow();
    }

    private boolean isOneToMany(Field field) {
        final OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        return Objects.nonNull(oneToMany);
    }
}
