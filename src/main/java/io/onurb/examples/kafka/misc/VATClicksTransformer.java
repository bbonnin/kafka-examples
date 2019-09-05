package io.onurb.examples.kafka.misc;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.kstream.Transformer;
import org.apache.kafka.streams.kstream.TransformerSupplier;
import org.apache.kafka.streams.processor.ProcessorContext;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.ValueAndTimestamp;

import java.util.Date;
import java.util.Optional;

public class VATClicksTransformer implements TransformerSupplier<String, Long, KeyValue<String, Long>> {

    private final String stateStoreName;

    public VATClicksTransformer(final String stateStoreName) {
        this.stateStoreName = stateStoreName;
    }

    @Override
    public Transformer<String, Long, KeyValue<String, Long>> get() {
        return new Transformer<String, Long, KeyValue<String, Long>>() {

            private KeyValueStore<String, ValueAndTimestamp<Long>> stateStore;

            @SuppressWarnings("unchecked")
            @Override
            public void init(final ProcessorContext context) {
                stateStore = (KeyValueStore<String, ValueAndTimestamp<Long>>) context.getStateStore(stateStoreName);
            }

            @Override
            public KeyValue<String, Long> transform(final String region, final Long clicks1) {
                final Optional<ValueAndTimestamp<Long>> vat = Optional.ofNullable(stateStore.get(region));
                final Long clicks2 = vat.orElse(ValueAndTimestamp.make(0L, 0)).value();

                System.out.println("TRANSFORM => " + region + ": " + clicks1 + " - " + clicks2);

                final Long total = clicks2 + clicks1;

                stateStore.put(region, ValueAndTimestamp.make(total, new Date().getTime()));

                return KeyValue.pair(region, total);
            }

            @Override
            public void close() {
            }
        };
    }
}
