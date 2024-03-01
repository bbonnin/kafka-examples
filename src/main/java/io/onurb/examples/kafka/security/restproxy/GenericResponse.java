package io.onurb.examples.kafka.security.restproxy;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GenericResponse {

    @JsonProperty("error_code")
    public int code;

    public String message;

    @Override
    public String toString() {
        return "code=" + code + ", message='" + message + '\'';
    }
}
