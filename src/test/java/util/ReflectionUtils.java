package util;

import java.lang.reflect.Field;

public class ReflectionUtils {
    private ReflectionUtils() {
        throw new AssertionError();
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        return null;
    }

    public static Object getFieldValue(Object instance, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        final Field field = instance.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(instance);
    }
}
