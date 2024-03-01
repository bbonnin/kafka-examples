package io.onurb.examples.kafka.security.serdes;

import io.onurb.examples.kafka.security.annotation.EncryptedData;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Simple way to deserialize JSON to POJO with encrypted fields or a full encryption of the message.
 * In this implementation, the encrypted fields are supposed to be string (for demo purpose).
 */
public class EncryptionDeserializer<T> implements Deserializer<T> {

    private Deserializer<T> theDeserializer;

    private Class<T> tClass;

    public EncryptionDeserializer(Deserializer<T> theDeserializer, Class<T> tClass) {
        this.theDeserializer = theDeserializer;
        this.tClass = tClass;
    }

    @Override
    public T deserialize(String topic, byte[] message) {
        return deserialize(topic, null, message);
    }

    @Override
    public T deserialize(String topic, Headers headers, byte[] message) {
        T data;

        if (tClass.isAnnotationPresent(EncryptedData.class)) {
            // The whole class has been encrypted.
            // So, decrypt the message before deserializing it.
            byte[] decryptedBytes = EncryptionUtils.decryptInstance(message);
            data = theDeserializer.deserialize(topic, headers, decryptedBytes);
        } else {
            // Other case: only some fields are encrypted.
            // So, first, deserialize the message, then decrypt these fields.
            data = theDeserializer.deserialize(topic, headers, message);
            EncryptionUtils.decryptFields(data, tClass);
        }

        return data;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        theDeserializer.configure(configs, isKey);
    }
}
