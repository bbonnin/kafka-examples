package io.onurb.examples.kafka;


import io.onurb.examples.kafka.serdes.JsonPOJODeserializer;
import io.onurb.examples.kafka.serdes.JsonPOJOSerializer;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Données enrichies envoyées dans le topic: user-region-clicks-topic (au format json).
 */
public class UserClicks {

    // Attributs publics pour simplifier :O

    public final String name;
    public final String region;
    public final long clicks;

    public UserClicks(String name, String region, long clicks) {
        this.name = name;
        this.region = region;
        this.clicks = clicks;
    }

    public static Serde<UserClicks> serdes() {
        final Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<UserClicks> serializer = new JsonPOJOSerializer<>();
        serdeProps.put("JsonPOJOClass", UserClicks.class);
        serializer.configure(serdeProps, false);

        final Deserializer<UserClicks> deserializer = new JsonPOJODeserializer<>();
        serdeProps.put("JsonPOJOClass", UserClicks.class);
        deserializer.configure(serdeProps, false);

        return Serdes.serdeFrom(serializer, deserializer);
    }
}
