package io.onurb.examples.kafka.security.restproxy;

/**
 * Used by key and value in the messages.
 */
public class Data<T> {

    public String type;

    public T data;

    public Data() {
        // For deserialization
    }

    public Data(String type, T data) {
        this.type = type;
        this.data = data;
    }
}
