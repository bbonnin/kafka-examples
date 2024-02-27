package io.onurb.examples.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsConfig;

import java.util.Properties;

public class Common {

    public static String USER_REGION_TOPIC = "user-region-topic";
    public static String USER_CLICKS_TOPIC = "user-clicks-topic";
    public static String USER_REGION_STORE = "user-region-store";
    public static String USER_REGION_CLICKS_TOPIC = "user-region-clicks-topic";
    public static String USER_REGION_CLICKS_TEMP_STORE = "user-region-clicks-temp-store";

    public static int CLICKS_THRESHOLD = 100;


    public static Properties commonProps(String appId) {
        final Properties props = new Properties();

        props.put(StreamsConfig.APPLICATION_ID_CONFIG, appId);
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.STATE_DIR_CONFIG, "./temp");
        props.put(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 1000);

        return props;
    }
}
