package io.onurb.examples.kafka.security.serdes;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import org.apache.kafka.common.header.Headers;

/**
 * The fields annotated with @EncryptField are encrypted before serialization.
 */
public class EncryptedJsonSchemaSerializer<T> extends KafkaJsonSchemaSerializer<T> {

    protected Class<T> tClass;

    @Override
    public byte[] serialize(String topic, Headers headers, T data) {
        EncryptionUtils.encryptFields(data, tClass);
        return super.serialize(topic, headers, data);
    }
}
