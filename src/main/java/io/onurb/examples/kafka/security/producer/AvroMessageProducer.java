package io.onurb.examples.kafka.security.producer;

import com.github.javafaker.Faker;
import io.confluent.kafka.serializers.AbstractKafkaSchemaSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import io.onurb.examples.kafka.security.avro.Payment;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

public class AvroMessageProducer {

    protected KafkaProducer<Long, Payment> producer;

    public static void main(String[] args) throws IOException {
        final AvroMessageProducer producer = new AvroMessageProducer();
        try {
            producer.init();
            producer.sendMessages("payments-avro", 10);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            producer.close();
        }
    }

    private void init() {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.Long().serializer().getClass().getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");
        props.put(AbstractKafkaSchemaSerDeConfig.AUTO_REGISTER_SCHEMAS, true);
        producer = new KafkaProducer<>(props);
    }

    private void sendMessages(String topic, int nb) throws IOException {
        final Faker faker = new Faker();

        for (int i = 0; i < nb; i++) {
            Payment payment = new Payment(
                    faker.name().fullName(),
                    faker.number().randomDouble(2, 1, 1_000_000),
                    faker.finance().iban().toUpperCase());

            try {
                System.out.println("Send new payment: " + payment);
                producer.send(new ProducerRecord<>(topic, faker.number().randomNumber(), payment)).get();
            }
            catch (Exception ex) {
                System.err.print(ex.getMessage());
                throw new IOException(ex);
            }
        }
    }

    private void close() {
        if (producer != null) {
            producer.close(Duration.ofMinutes(1));
        }
    }
}
