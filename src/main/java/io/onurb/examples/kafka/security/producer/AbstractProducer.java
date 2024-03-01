package io.onurb.examples.kafka.security.producer;

import com.github.javafaker.Faker;
import io.onurb.examples.kafka.security.Payment;
import io.onurb.examples.kafka.security.serdes.PaymentSerdes;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Properties;

public class AbstractProducer {

    protected KafkaProducer<Long, Payment> producer;

    protected void init() {
        init(Map.of());
    }

    protected void init(Map<?, ?> otherProps) {
        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.Long().serializer().getClass().getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PaymentSerdes.Serializer.class.getName());
        props.putAll(otherProps);
        producer = new KafkaProducer<>(props);
    }

    protected void sendMessages(String topic, int nb) throws IOException {
        final Faker faker = new Faker();

        for (int i = 0; i < nb; i++) {
            Payment payment = new Payment(
                    faker.number().randomDouble(2, 1, 1_000_000),
                    faker.name().fullName(),
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

    protected void close() {
        producer.close(Duration.ofMinutes(1));
    }
}
