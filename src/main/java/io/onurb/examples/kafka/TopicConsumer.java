package io.onurb.examples.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;

import java.time.Duration;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Consommateur de topic.
 *
 * Arguments: "nom du topic" "type du deserialiseur (string, long)"
 */
public class TopicConsumer {

    private static Map<String, Deserializer> DESERIALIZERS = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>("string", Serdes.String().deserializer()),
            new AbstractMap.SimpleImmutableEntry<>("long", Serdes.Long().deserializer()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


    public static void main(String[] args) {

        if (args.length < 2) {
            System.err.println("Missing arg: topic name and/or deserializer type");
            System.exit(1);
        }

        final String topic = args[0];
        final Deserializer deserializer = DESERIALIZERS.get(args[1]);

        final Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, TopicConsumer.class.getSimpleName() + "-" + topic);
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.String().deserializer().getClass().getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, deserializer.getClass().getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        pollMessages(properties, topic, deserializer);
    }

    private static <T> void pollMessages(Properties properties, String topic, Deserializer<T> deserilizer) {

        final KafkaConsumer<String, T> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Arrays.asList(topic));

        while (true) {
            ConsumerRecords<String, T> records = consumer.poll(Duration.ofMillis(200));

            if (records.count() > 0) {
                for (ConsumerRecord<String, T> record: records) {
                    System.out.println(record.key() + ": " + record.value());
                }
            }
        }

    }
}
