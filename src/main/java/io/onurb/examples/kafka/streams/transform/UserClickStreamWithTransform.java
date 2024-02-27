package io.onurb.examples.kafka.streams.transform;

import io.onurb.examples.kafka.streams.UserClicks;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.Produced;

import java.util.Properties;
import java.util.concurrent.CountDownLatch;

import static io.onurb.examples.kafka.streams.Common.*;

/**
 * Définition d'un stream avec transform (recupération d'une donnée dans un state store et enrichissement des données d'un topic).
 */
public class UserClickStreamWithTransform {

    private static KafkaStreams streams;


    public static void main(String[] args) {

        final Properties props = commonProps(UserClickStreamWithTransform.class.getSimpleName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());

        final StreamsBuilder builder = new StreamsBuilder();

        //
        // Global table matérialisé par un state store (contient des données de référence).
        //
        builder.globalTable(
                USER_REGION_TOPIC,
                Consumed.with(Serdes.String(), Serdes.String()),
                Materialized.as(USER_REGION_STORE));

        //
        // Enrichissement d'un stream avec l'info du topic de référence matérialisé dans le state store ci-dessus
        //
        builder.stream(USER_CLICKS_TOPIC, Consumed.with(Serdes.String(), Serdes.Long()))
                .transform(new UserClicksTransformer(USER_REGION_STORE))
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
