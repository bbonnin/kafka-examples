package io.onurb.examples.kafka.streams;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.onurb.examples.kafka.streams.Common.USER_REGION_TOPIC;

/**
 * Producteur des données pour le topic de référence: user-region-topic.
 */
public class UserRegionProducer {

    private static Map<String, String> USER_REGIONS = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>("alice", "CA"),
            new AbstractMap.SimpleImmutableEntry<>("bob", "NY"),
            new AbstractMap.SimpleImmutableEntry<>("charlie", "CA"),
            new AbstractMap.SimpleImmutableEntry<>("dave", "NY"),
            new AbstractMap.SimpleImmutableEntry<>("ed", "FL"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    public static void main(String[] args) throws IOException {

        final Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass().getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass().getName());

        final KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        USER_REGIONS.forEach((user, region) -> {
            System.out.println("Send " + user + ":" + region);
            try {
                producer.send(new ProducerRecord<>(USER_REGION_TOPIC, user, region)).get();
            }
            catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        producer.close();
    }
}
