package io.onurb.examples.kafka.transform;

import io.onurb.examples.kafka.UserClicks;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;


/**
 * Transformateur: d'un info simple à une info enrichie.
 */
public class UserClicksTransformer implements TransformerSupplier<String, Long, KeyValue<String, UserClicks>> {

    private final String regionStoreName;

    public UserClicksTransformer(final String regionStoreName) {
        this.regionStoreName = regionStoreName;
    }

    @Override
    public Transformer<String, Long, KeyValue<String, UserClicks>> get() {
        return new Transformer<String, Long, KeyValue<String, UserClicks>>() {

            private KeyValueStore<String, ValueAndTimestamp<String>> regionStore;

            @SuppressWarnings("unchecked")
            @Override
            public void init(final ProcessorContext context) {
                regionStore = (KeyValueStore<String, ValueAndTimestamp<String>>) context.getStateStore(regionStoreName);
            }

            @Override
            public KeyValue<String, UserClicks> transform(final String user, final Long clicks) {
                final String region = regionStore.get(user).value(); // TODO: géré le cas où la donnée n'est pas dans le store

                System.out.println("TRANSFORM => " + user + ": " + user + " - " + region);

                return KeyValue.pair(user, new UserClicks(user, region, clicks));
            }

            @Override
            public void close() {
            }
        };
    }
}
