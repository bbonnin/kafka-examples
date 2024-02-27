package io.onurb.examples.kafka.common.serdes;

import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.serialization.Deserializer;

import java.util.Map;

/**
 * Source: https://github.com/apache/kafka/blob/1.0/streams/examples/src/main/java/org/apache/kafka/streams/examples/pageview/JsonPOJODeserializer.java
 */
public class JsonDeserializer<T> extends AbstractJsonSerdes<T> implements Deserializer<T> {

    /**
     * Default constructor needed by Kafka
     */
    public JsonDeserializer() {
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Map<String, ?> props, boolean isKey) {
        if (props.containsKey("JsonPOJOClass")) {
            tClass = (Class<T>) props.get("JsonPOJOClass");
        }
    }

    @Override
    public T deserialize(String topic, byte[] bytes) {
        if (bytes == null) {
            return null;
        }

        try {
            return objectMapper.readValue(bytes, tClass);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
    }
}
