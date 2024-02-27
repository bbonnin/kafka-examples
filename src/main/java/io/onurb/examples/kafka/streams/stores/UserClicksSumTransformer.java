package io.onurb.examples.kafka.streams.stores;

import io.onurb.examples.kafka.streams.UserClicks;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

import static io.onurb.examples.kafka.streams.Common.CLICKS_THRESHOLD;


/**
 * Transformateur: d'un info simple à une info enrichie (avec la region et la somme des clics).
 */
public class UserClicksSumTransformer implements TransformerSupplier<String, Long, KeyValue<String, UserClicks>> {

    private final String regionStoreName;

    private final String tempStoreName;


    public UserClicksSumTransformer(final String regionStoreName, final String tempStoreName) {
        this.regionStoreName = regionStoreName;
        this.tempStoreName = tempStoreName;
    }

    @Override
    public Transformer<String, Long, KeyValue<String, UserClicks>> get() {
        return new Transformer<String, Long, KeyValue<String, UserClicks>>() {

            private KeyValueStore<String, ValueAndTimestamp<String>> regionStore;

            private KeyValueStore<String, UserClicks> tempStore;

            @SuppressWarnings("unchecked")
            @Override
            public void init(final ProcessorContext context) {
                regionStore = (KeyValueStore<String, ValueAndTimestamp<String>>) context.getStateStore(regionStoreName);
                tempStore = (KeyValueStore<String, UserClicks>) context.getStateStore(tempStoreName);
            }

            @Override
            public KeyValue<String, UserClicks> transform(final String user, final Long clicks) {

                UserClicks userClicks = tempStore.get(user);

                if (userClicks != null) {
                    System.out.println("TRANSFORM:  USER DEJA DANS LE STORE => " + user + ": " + user + " - " + userClicks.clicks);
                    userClicks.clicks += clicks;
                }
                else {
                    System.out.println("TRANSFORM: USER INEXISTANT => " + user + ": " + user );
                    final String region = regionStore.get(user).value(); // TODO: géré le cas où la donnée n'est pas dans le store
                    userClicks = new UserClicks(user, region, clicks);
                }

                if (userClicks.clicks < CLICKS_THRESHOLD) {
                    tempStore.put(user, userClicks);
                }
                else {
                    tempStore.delete(user);
                }

                return KeyValue.pair(user, userClicks);
            }

            @Override
            public void close() {
            }
        };
    }
}
