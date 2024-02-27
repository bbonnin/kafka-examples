package io.onurb.examples.kafka.security;

import com.github.javafaker.Faker;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.onurb.examples.kafka.security.serdes.PaymentSerdes;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.util.Properties;

public class SecuredMessageWithSchemaRegistryProducer {

    public static void main(String[] args) throws IOException {

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.Long().serializer().getClass().getName());
        //props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaJsonSchemaSerializer.class.getName()); // Default serializer when using schema registry
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PaymentSerdes.JsonSchemaSerializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, false); // Best practice

        final KafkaProducer<Long, Payment> producer = new KafkaProducer<>(props);

        final Faker faker = new Faker();

        for (int i = 0; i < 10; i++) {
            Payment payment = new Payment(
                    faker.number().randomDouble(2, 1, 1_000_000),
                    faker.name().fullName(),
                    faker.finance().iban().toUpperCase());

            try {
                System.out.println("Send new payment: " + payment);
                producer.send(new ProducerRecord<>("payments", faker.number().randomNumber(), payment)).get();
            }
            catch (Exception ex) {
                System.err.print(ex.getMessage());
                throw new IOException(ex);
            }
        }
    }
}
