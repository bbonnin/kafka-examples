package io.onurb.examples.kafka.misc;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class StreamTest {

    public static void main(String[] args) {

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, StreamTest.class.getSimpleName());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.STATE_DIR_CONFIG, "./temp");
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 5000);

        System.out.println(props);

        StreamsBuilder builder = new StreamsBuilder();

        Materialized<String, Long, KeyValueStore<Bytes, byte[]>> store = Materialized.<String, Long, KeyValueStore<Bytes, byte[]>>as("word-counts-store");
        store.withKeySerde(Serdes.String());
        store.withValueSerde(Serdes.Long());

        GlobalKTable countTable = builder.globalTable("word-counts-topic", store);

        KStream<String, String> textLines = builder.stream("word-topic");
        KTable<String, Long> wordCounts = textLines
                .flatMapValues(textLine -> Arrays.asList(textLine.toLowerCase().split("\\W+")))
                .groupBy((key, word) -> word)
                .count(store)
                ;
                //.leftJoin()
        wordCounts.toStream().to("word-counts-topic", Produced.with(Serdes.String(), Serdes.Long()));

        KafkaStreams streams = new KafkaStreams(builder.build(), props);

        CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                streams.close();
                latch.countDown();
            }
        });

        try {
            streams.start();
            latch.await();
        }
        catch (final Throwable e) {
            System.exit(1);
        }

        System.exit(0);
    }
}
