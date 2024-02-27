package io.onurb.examples.kafka.security.serdes;

import io.onurb.examples.kafka.security.Cryptor;
import io.onurb.examples.kafka.security.KMS;
import io.onurb.examples.kafka.security.annotation.EncryptedField;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.util.HashMap;
import java.util.Map;

import static io.onurb.examples.kafka.common.ReflectUtils.readField;
import static io.onurb.examples.kafka.common.ReflectUtils.writeField;
import static io.onurb.examples.kafka.security.Constants.DEMO_ENCRYPTED_FIELD_KEY_NAME;
import static io.onurb.examples.kafka.security.Constants.DEMO_ENCRYPTED_FIELD_SALT;
import static java.nio.charset.StandardCharsets.UTF_8;

public final class EncryptionUtils {

    public static byte[] encryptInstance(byte[] data) {
        return Cryptor.encrypt(
                data,
                KMS.getKey(DEMO_ENCRYPTED_FIELD_KEY_NAME),
                DEMO_ENCRYPTED_FIELD_SALT);
    }

    public static byte[] decryptInstance(byte[] cryptedInstance) {
        return Cryptor.decrypt(
                cryptedInstance,
                KMS.getKey(DEMO_ENCRYPTED_FIELD_KEY_NAME),
                DEMO_ENCRYPTED_FIELD_SALT);
    }

    public static <T> void decryptFields(T data, Class<T> tClass) {
        FieldUtils.getFieldsListWithAnnotation(tClass, EncryptedField.class)
                .stream()
                .filter(field -> readField(data, field.getName()) != null)
                .forEach(field -> {
                    Object fieldValue = readField(data, field.getName());

                    // For each field, decrypt them
                    byte[] decryptedValue = Cryptor.decrypt(
                            fieldValue.toString(),
                            KMS.getKey(DEMO_ENCRYPTED_FIELD_KEY_NAME),
                            DEMO_ENCRYPTED_FIELD_SALT);

                    // Use this new value (we suppose it's a string for a demo purpose)
                    writeField(data, field.getName(), new String(decryptedValue, UTF_8));
                });
    }

    public static <T> void encryptFields(T data, Class<T> tClass) {
        Map<String, Object> values = new HashMap<>(); // Unused at the moment, but it could be reused to rebuild the source data

        // Get crypted fields
        FieldUtils.getFieldsListWithAnnotation(tClass, EncryptedField.class)
                .stream()
                .filter(field -> readField(data, field.getName()) != null)
                .forEach(field -> {
                    Object fieldValue = readField(data, field.getName());

                    // Save each value
                    values.put(field.getName(), fieldValue);

                    // For each field, crypt them
                    byte[] encryptedValue = Cryptor.encrypt(
                            fieldValue.toString(),
                            KMS.getKey(DEMO_ENCRYPTED_FIELD_KEY_NAME),
                            DEMO_ENCRYPTED_FIELD_SALT);

                    // Use this new value (we suppose it's a string for a demo purpose)
                    writeField(data, field.getName(), new String(encryptedValue, UTF_8));
                });
    }
}
