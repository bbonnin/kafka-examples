package io.onurb.examples.kafka.security.serdes;

import io.onurb.examples.kafka.common.serdes.JsonSerializer;
import io.onurb.examples.kafka.security.annotation.EncryptedData;

/**
 * Simple way to serialize POJO in JSON with encrypted fields (or the whole string representation of the JSON).
 * In this implementation, the encrypted fields are supposed to be string (for sure, needs an improvement).
 */
public class EncryptedJsonSerializer<T> extends JsonSerializer<T> {

    @Override
    public byte[] serialize(String topic, T data) {
        if (data.getClass().isAnnotationPresent(EncryptedData.class)) {
            // The whole class will be encrypted
            // For simplifying the process: generate JSON, get the string representation, then encrypt it
            byte[] serializedData = super.serialize(topic, data);
            return EncryptionUtils.encryptInstance(serializedData);
        }

        // Otherwise, encrypt some fields
        EncryptionUtils.encryptFields(data, tClass);
        return super.serialize(topic, data);
    }
}
