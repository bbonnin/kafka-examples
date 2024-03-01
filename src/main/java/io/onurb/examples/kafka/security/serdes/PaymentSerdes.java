package io.onurb.examples.kafka.security.serdes;

import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.onurb.examples.kafka.common.serdes.JsonDeserializer;
import io.onurb.examples.kafka.common.serdes.JsonSerializer;
import io.onurb.examples.kafka.security.Payment;

public final class PaymentSerdes {

    public static class Deserializer extends EncryptionDeserializer<Payment> {
        public Deserializer() {
            super(new JsonDeserializer<>(Payment.class), Payment.class);
        }
    }

    public static class Serializer extends EncrytionSerializer<Payment> {
        public Serializer() {
            super(new JsonSerializer<>(), Payment.class);
        }
    }

    public static class JsonSchemaBasedDeserializer extends EncryptionDeserializer<Payment> {
        public JsonSchemaBasedDeserializer() {
            super(new KafkaJsonSchemaDeserializer<>(), Payment.class);
        }
    }

    public static class JsonSchemaBasedSerializer extends EncrytionSerializer<Payment> {
        public JsonSchemaBasedSerializer() {
            super(new KafkaJsonSchemaSerializer<>(), Payment.class);
        }
    }
}
