package io.onurb.examples.kafka.streams;


import io.onurb.examples.kafka.common.serdes.JsonDeserializer;
import io.onurb.examples.kafka.common.serdes.JsonSerializer;
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

    public String name;
    public String region;
    public long clicks;

    public UserClicks() {}

    public UserClicks(String name, String region, long clicks) {
        this.name = name;
        this.region = region;
        this.clicks = clicks;
    }

    public static Serde<UserClicks> serdes() {
        final Map<String, Object> serdeProps = new HashMap<>();

        final Serializer<UserClicks> serializer = new JsonSerializer<>();
        serdeProps.put("JsonPOJOClass", UserClicks.class);
        serializer.configure(serdeProps, false);

        final Deserializer<UserClicks> deserializer = new JsonDeserializer<>();
        serdeProps.put("JsonPOJOClass", UserClicks.class);
        deserializer.configure(serdeProps, false);

        return Serdes.serdeFrom(serializer, deserializer);
    }
}
