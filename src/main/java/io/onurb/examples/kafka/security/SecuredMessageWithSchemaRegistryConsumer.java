package io.onurb.examples.kafka.security;

import io.confluent.kafka.serializers.KafkaJsonDeserializerConfig;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaDeserializer;
import io.confluent.kafka.serializers.json.KafkaJsonSchemaSerializer;
import io.onurb.examples.kafka.security.serdes.PaymentSerdes;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.Serdes;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class SecuredMessageWithSchemaRegistryConsumer {

    public static void main(String[] args) {

        final String topic = "payments";

        final Properties props = new Properties();

        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "secured-payments-consumers");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, Serdes.Long().deserializer().getClass().getName());
        //props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaJsonSchemaDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, PaymentSerdes.JsonSchemaDeserializer.class.getName());
        props.put("schema.registry.url", "http://localhost:8081");
        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest"); //"earliest"); // To ignore previous badly formatted data
        props.put(KafkaJsonDeserializerConfig.JSON_VALUE_TYPE, Payment.class.getName());


        final KafkaConsumer<Long, Payment> consumer = new KafkaConsumer<>(props);

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
