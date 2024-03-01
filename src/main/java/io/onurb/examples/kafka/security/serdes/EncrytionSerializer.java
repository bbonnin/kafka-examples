package io.onurb.examples.kafka.security.serdes;

import io.onurb.examples.kafka.security.annotation.EncryptedData;
import org.apache.kafka.common.header.Headers;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

/**
 * Simple way to serialize POJO in JSON with encrypted fields (or the whole string representation of the JSON).
 * In this implementation, the encrypted fields are supposed to be string (for sure, needs an improvement).
 */
public abstract class EncrytionSerializer<T> implements Serializer<T> {

    //SchemaRegistryClient c;

    private Serializer<T> theSerializer;

    private Class<T> tClass;

    public EncrytionSerializer(Serializer<T> theSerializer, Class<T> tClass) {
        this.theSerializer = theSerializer;
        this.tClass = tClass;
    }

    @Override
    public byte[] serialize(String topic, T data) {
        return serialize(topic, null, data);
    }

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        if (data.getClass().isAnnotationPresent(EncryptedData.class)) {
            // The whole class will be encrypted
            // For simplifying the process: generate JSON, get the string representation, then encrypt it
            byte[] serializedData = theSerializer.serialize(topic, headers, data);
            return EncryptionUtils.encryptInstance(serializedData);
        }

        // Otherwise, encrypt some fields
        EncryptionUtils.encryptFields(data, tClass);
        return theSerializer.serialize(topic, headers, data);
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        theSerializer.configure(configs, isKey);
    }
}
