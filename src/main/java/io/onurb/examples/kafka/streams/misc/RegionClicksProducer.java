package io.onurb.examples.kafka.streams.misc;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Serdes;

import java.io.IOException;
import java.util.Properties;

public class RegionClicksProducer {

    public static void main(String[] args) throws IOException {

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Serdes.String().serializer().getClass().getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Serdes.Long().serializer().getClass().getName());

        KafkaProducer<String, Long> producer = new KafkaProducer<>(props);

        try {
            producer.send(new ProducerRecord<>("region-clicks-topic", "FL", 500L)).get();
        }
        catch (Exception ex) {
            System.out.print(ex.getMessage());
            throw new IOException(ex.toString());
        }
    }
}
