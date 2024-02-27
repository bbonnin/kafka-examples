package io.onurb.examples.kafka.security.serdes;

import io.onurb.examples.kafka.security.Payment;

public final class PaymentSerdes {

    public static class Deserializer extends EncryptedJsonDeserializer<Payment> {
        public Deserializer() {
            tClass = Payment.class;
        }
    }

    public static class Serializer extends EncryptedJsonSerializer<Payment> {
        public Serializer() {
            tClass = Payment.class;
        }
    }

    public static class JsonSchemaDeserializer extends EncryptedJsonSchemaDeserializer<Payment> {
        public JsonSchemaDeserializer() {
            tClass = Payment.class;
        }
    }

    public static class JsonSchemaSerializer extends EncryptedJsonSchemaSerializer<Payment> {
        public JsonSchemaSerializer() {
            tClass = Payment.class;
        }
    }
}
