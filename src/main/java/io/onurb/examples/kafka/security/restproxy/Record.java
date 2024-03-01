package io.onurb.examples.kafka.security.restproxy;

public class Record<V> {

    public V value;

    public Record(V value) {
        this.value = value;
    }
}
