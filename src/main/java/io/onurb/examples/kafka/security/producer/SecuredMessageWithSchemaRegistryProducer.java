package io.onurb.examples.kafka.security.producer;

import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.onurb.examples.kafka.security.serdes.PaymentSerdes;
import org.apache.kafka.clients.producer.ProducerConfig;

import java.io.IOException;
import java.util.Properties;

public class SecuredMessageWithSchemaRegistryProducer extends AbstractProducer {

    public static void main(String[] args) throws IOException {
        SecuredMessageProducer producer = new SecuredMessageProducer();
        try {
            final Properties props = new Properties();
            //props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaSerializer.class.getName()); // Default serializer when using schema registry
            props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PaymentSerdes.JsonSchemaBasedSerializer.class.getName());
            props.put("schema.registry.url", "http://localhost:8081");
            props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false); // Best practice
            producer.init(props);

            producer.sendMessages("payments", 10);
        }
        finally {
            producer.close();
        }
    }
}
