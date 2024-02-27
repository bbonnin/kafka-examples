package io.onurb.examples.kafka.streams.misc;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.errors.InvalidStateStoreException;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/*
kafka-topics --zookeeper localhost:2181 --create --topic region-clicks-store --replication-factor 1 --partitions 1 \
   --config min.insync.replicas=1 --config cleanup.policy=compact

kafka-topics --zookeeper localhost:2181 --list
*/

public class UserClickStreamTest {

    private static Map<String, String> USER_REGIONS = Stream.of(
            new AbstractMap.SimpleImmutableEntry<>("alice", "CA"),
            new AbstractMap.SimpleImmutableEntry<>("bob", "NY"),
            new AbstractMap.SimpleImmutableEntry<>("charlie", "CA"),
            new AbstractMap.SimpleImmutableEntry<>("dave", "NY"),
            new AbstractMap.SimpleImmutableEntry<>("ed", "FL"))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    private static KafkaStreams streams;

    public static void main(String[] args) {

        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, UserClickStreamTest.class.getSimpleName());
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        props.put(StreamsConfig.STATE_DIR_CONFIG, "./temp");
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);

        final StreamsBuilder builder = new StreamsBuilder();

        final StoreBuilder<KeyValueStore<String, Long>> persistStoreEx1 = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("region-clicks-store-persist"),
                Serdes.String(),
                Serdes.Long())
                .withCachingEnabled();

        builder.addStateStore(persistStoreEx1);

        final StoreBuilder<KeyValueStore<String, Long>> memStoreEx1 = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore("region-clicks-store-mem"),
                Serdes.String(),
                Serdes.Long())
                .withCachingEnabled();

        builder.addStateStore(memStoreEx1);

        //final GlobalKTable<String, Long> regionClicksTable = builder.globalTable("region-clicks-topic");

        Materialized<String, Long, KeyValueStore<Bytes, byte[]>> storeEx2 = Materialized.as("region-clicks-store-ex2");
        builder.table("region-clicks-topic", storeEx2);

        builder.stream("user-clicks-topic", Consumed.with(Serdes.String(), Serdes.Long()))
                .map((user, clicks) -> {
                    System.out.println("MAP (GET REGION) => " + USER_REGIONS.get(user) + ": " + clicks);
                    return new KeyValue<>(USER_REGIONS.get(user), clicks);
                })


                // EXEMPLE 1 - TRANSFORMATION UTILISANT UN STORE STOCKANT LES VALEURS REFERENCES
                .transform(new ClicksTransformer("region-clicks-store-persist"), "region-clicks-store-persist")
                .to("region-clicks-topic")

                // EXEMPLE 2 - UTILISATION D'UN STORE MATERIALISATION D'UN TOPIC
                //.transform(new VATClicksTransformer("region-clicks-store-ex2"), "region-clicks-store-ex2")
                //.to("region-clicks-topic")


                ;

        streams = new KafkaStreams(builder.build(), props);

        final CountDownLatch latch = new CountDownLatch(1);

        Runtime.getRuntime().addShutdownHook(new Thread("streams-user-clicks-shutdown-hook") {
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

    public static <T> T waitUntilStoreIsQueryable(final String storeName,
                                                  final QueryableStoreType<T> queryableStoreType) throws InterruptedException {
        while (true) {
            try {
                return streams.store(storeName, queryableStoreType);
            }
            catch (InvalidStateStoreException ignored) {
                // store not yet ready for querying
                Thread.sleep(100);
            }
        }
    }
}

/*.leftJoin(regionClicksTable,
                        (region, clicks) -> {
                            System.out.println("JOIN VALUE MAPPER => " + region + ": " + clicks);
                            return region;
                        },
                        (clicks1, clicks2) -> {
                            System.out.println("JOIN VALUE JOINER => " + clicks1 + " - " + clicks2);
                            clicks1 = clicks1 == null ? 0 : clicks1;
                            clicks2 = clicks2 == null ? 0 : clicks2;
                            return clicks1 + clicks2;
                        })*/

//.groupByKey()
//.reduce((clicks1, clicks2) -> clicks1 + clicks2)
//.toStream()