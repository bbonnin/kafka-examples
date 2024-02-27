package io.onurb.examples.kafka.common.serdes;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractJsonSerdes<T> {

    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected Class<T> tClass;
}
