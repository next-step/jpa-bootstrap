package persistence.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

public class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static Class<?> collectionClass(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();

            if (typeArguments != null && typeArguments.length > 0) {
                Type typeArgument = typeArguments[0];
                try {
                    return Class.forName(typeArgument.getTypeName());
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        throw new IllegalArgumentException();
    }

    @SuppressWarnings("unchecked")
    public static Class<? extends Collection<Object>> getCollectionFieldType(Field field) {
        if (Collection.class.isAssignableFrom(field.getType())) {
            return (Class<? extends Collection<Object>>) field.getType();
        }
        throw new IllegalArgumentException("Field is not a Collection type");
    }

    public static <T> boolean hasGenericType(Class<?> aClass, Class<T> entityClass) {
        Type[] genericInterfaces = aClass.getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface instanceof ParameterizedType parameterizedType) {
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (Type actualTypeArgument : actualTypeArguments) {
                    if (actualTypeArgument.getTypeName().equals(entityClass.getTypeName())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
