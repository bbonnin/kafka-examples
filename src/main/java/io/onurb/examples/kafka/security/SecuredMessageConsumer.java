package io.onurb.examples.kafka.security;

import io.onurb.examples.kafka.security.serdes.PaymentSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class SecuredMessageConsumer {

    public static void main(String[] args) {

        final String topic = "payments-no-schema";

        final Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "secured-payments-consumers");
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.Long().deserializer().getClass().getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PaymentSerdes.Deserializer.class.getName());
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); //"earliest"); // To ignore previous badly formatted data

        final KafkaConsumer<Long, Payment> consumer = new KafkaConsumer<>(properties);

        consumer.subscribe(Arrays.asList(topic));

        while (true) {
            ConsumerRecords<Long, Payment> records = consumer.poll(Duration.ofMillis(200));

            if (records.count() > 0) {
                for (ConsumerRecord<Long, Payment> record: records) {
                    System.out.println("New payment: " + record.value());
                }
            }
        }
    }
}
