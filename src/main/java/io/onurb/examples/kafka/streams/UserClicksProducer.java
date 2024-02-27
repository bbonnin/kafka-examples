package io.onurb.examples.kafka.streams;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 * Producteur des données temps réel (celles qui seront enrichies) envoyées dans le topic: user-clicks-topic
 */
public class UserClicksProducer {

    public static void main(String[] args) throws IOException {

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass().getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.Long().serializer().getClass().getName());

        final KafkaProducer<String, Long> producer = new KafkaProducer<>(props);

        final Random random = new Random();
        final String[] users = new String[] { "alice", "bob", "charlie", "dave", "ed" };

        for (int i = 0; i < 10; i++) {
            String user = users[random.nextInt(users.length)];

            try {
                long val = 10;
                System.out.println("Send " + user + ":" + val);
                producer.send(new ProducerRecord<>("user-clicks-topic", user, val)).get();
            }
            catch (Exception ex) {
                System.out.print(ex.getMessage());
                throw new IOException(ex.toString());
            }
        }
    }
}
