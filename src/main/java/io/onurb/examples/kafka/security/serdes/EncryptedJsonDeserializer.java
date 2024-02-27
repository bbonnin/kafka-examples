package io.onurb.examples.kafka.security.serdes;

import io.onurb.examples.kafka.common.serdes.JsonDeserializer;
import io.onurb.examples.kafka.security.annotation.EncryptedData;

/**
 * Simple way to deserialize POJO in JSON with encrypted fields.
 * In this implementation, the encrypted fields are supposed to be string (needs an improvement).
 */
public class EncryptedJsonDeserializer<T> extends JsonDeserializer<T> {

    @Override
    public T deserialize(String topic, byte[] bytes) {
        T data;

        if (tClass.isAnnotationPresent(EncryptedData.class)) {
            // The whole class has been encrypted
            // So, first, decrypt the message
            byte[] decryptedBytes = EncryptionUtils.decryptInstance(bytes);
            data = super.deserialize(topic, decryptedBytes);
        } else {
            // Other case: only some fields are crypted
            data = super.deserialize(topic, bytes);
            EncryptionUtils.decryptFields(data, tClass);
        }

        return data;
    }
}
