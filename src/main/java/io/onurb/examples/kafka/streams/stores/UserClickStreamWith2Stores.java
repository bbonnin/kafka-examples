package io.onurb.examples.kafka.streams.stores;

import io.onurb.examples.kafka.streams.UserClicks;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static io.onurb.examples.kafka.streams.Common.*;

/**
 * Exemple de stream avec 2 stores: 1 pour des données temporaires, 1 qui est la matérialisation des données d'un topic.
 */
public class UserClickStreamWith2Stores {

    private static KafkaStreams streams;


    public static void main(String[] args) {

        final Properties props = commonProps(UserClickStreamWith2Stores.class.getSimpleName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());

        final StreamsBuilder builder = new StreamsBuilder();

        //
        // Création d'un state store persistent pour les données temporaires (incomplètes)
        // Alternative: store in memory => Stores.inMemoryKeyValueStore()
        //
        final StoreBuilder<KeyValueStore<String, UserClicks>> tempStore = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(USER_REGION_CLICKS_TEMP_STORE),
                Serdes.String(),
                UserClicks.serdes())
                .withCachingEnabled();

        builder.addStateStore(tempStore);

        //
        // Global table matérialisé par un state store (contient des données de référence).
        //
        builder.globalTable(
                USER_REGION_TOPIC,
                Consumed.with(Serdes.String(), Serdes.String()),
                Materialized.as(USER_REGION_STORE));

        //
        // Stream processing: on branche suivant l'état: soit on envoie sur le topic final, soit on stocke l'état courant dans le store temporaire
        //
        builder.stream(USER_CLICKS_TOPIC, Consumed.with(Serdes.String(), Serdes.Long()))
                .transform(new UserClicksSumTransformer(USER_REGION_STORE, USER_REGION_CLICKS_TEMP_STORE), USER_REGION_CLICKS_TEMP_STORE)
                .filter((user, userClicks) -> userClicks.clicks >= CLICKS_THRESHOLD)
                .to(USER_REGION_CLICKS_TOPIC, Produced.with(Serdes.String(), UserClicks.serdes()));

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
}
