package io.onurb.examples.kafka.security.serdes;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import org.apache.kafka.common.header.Headers;

/**
 * The fields annotated with @EncryptField are decrypted after deserialization.
 */
public class EncryptedJsonSchemaDeserializer<T> extends KafkaJsonSchemaDeserializer<T> {

    protected Class<T> tClass;

    @Override
    public T deserialize(String topic, Headers headers, byte[] bytes) {
        T data = super.deserialize(topic, headers, bytes);
        EncryptionUtils.decryptFields(data, tClass);
        return data;
    }
}
