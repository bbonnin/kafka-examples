package io.onurb.examples.kafka.security.consumer;

import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.onurb.examples.kafka.security.Payment;
import io.onurb.examples.kafka.security.serdes.PaymentSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Properties;

public class SecuredMessageWithSchemaRegistryConsumer {

    public static void main(String[] args) {
        final SecuredMessageConsumer consumer = new SecuredMessageConsumer();
        try {
            final Properties props = new Properties();
            props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PaymentSerdes.JsonSchemaBasedDeserializer.class.getName());
            props.put("schema.registry.url", "http://localhost:8081");
            props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); //"earliest"); // To ignore previous badly formatted data
            props.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, Payment.class.getName());
            consumer.init(props);

            consumer.consumeMessages("payments");
        }
        finally {
            consumer.close();
        }
    }
}
