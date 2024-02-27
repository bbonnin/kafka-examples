package io.onurb.examples.kafka.streams.misc;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;

import java.util.Optional;

public class ClicksTransformer implements TransformerSupplier<String, Long, KeyValue<String, Long>> {

    private final String stateStoreName;

    public ClicksTransformer(final String stateStoreName) {
        this.stateStoreName = stateStoreName;
    }

    @Override
    public Transformer<String, Long, KeyValue<String, Long>> get() {
        return new Transformer<String, Long, KeyValue<String, Long>>() {

            private KeyValueStore<String, Long> stateStore;

            @SuppressWarnings("unchecked")
            @Override
            public void init(final ProcessorContext context) {
                stateStore = (KeyValueStore<String, Long>) context.getStateStore(stateStoreName);
            }

            @Override
            public KeyValue<String, Long> transform(final String region, final Long clicks1) {
                final Optional<Long> clicks2 = Optional.ofNullable(stateStore.get(region));

                System.out.println("TRANSFORM => " + region + ": " + clicks1 + " - " + clicks2.orElse(0L));

                final Long total = clicks2.orElse(0L) + clicks1;

                stateStore.put(region, total);

                return KeyValue.pair(region, total);
            }

            @Override
            public void close() {
            }
        };
    }
}
