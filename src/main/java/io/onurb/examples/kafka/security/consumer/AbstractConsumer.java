package io.onurb.examples.kafka.security.consumer;

import io.onurb.examples.kafka.security.Payment;
import io.onurb.examples.kafka.security.serdes.PaymentSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class AbstractConsumer {

    protected KafkaConsumer<Long, Payment> consumer;

    protected void init() {
        init(Map.of());
    }

    protected void init(Map<?, ?> otherProps) {
        final Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "secured-payments-consumers");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.Long().deserializer().getClass().getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PaymentSerdes.Deserializer.class.getName());
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); //"earliest"); // To ignore previous badly formatted data
        props.putAll(otherProps);
        consumer = new KafkaConsumer<>(props);
    }

    protected void consumeMessages(String topic) {
        consumer.subscribe(List.of(topic));

        while (true) {
            ConsumerRecords<Long, Payment> records = consumer.poll(Duration.ofMillis(200));

            if (records.count() > 0) {
                for (ConsumerRecord<Long, Payment> record: records) {
                    System.out.println("New payment: " + record.value());
                }
            }
        }
    }

    protected void close() {
        consumer.close(Duration.ofMinutes(1));
    }
}
