package io.onurb.examples.kafka.common;

import org.apache.commons.lang3.reflect.FieldUtils;

public final class ReflectUtils {

    public static Object readField(Object data, String fieldName) {
        try {
            return FieldUtils.readDeclaredField(data, fieldName, true);
        } catch (IllegalAccessException e) {
            // Ignore
            return null;
        }
    }

    public static void writeField(Object data, String fieldName, Object fieldValue) {
        try {
            FieldUtils.writeDeclaredField(data, fieldName, fieldValue, true);
        } catch (IllegalAccessException e) {
            // Ignore
        }
    }
}
