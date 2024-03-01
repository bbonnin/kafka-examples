package io.onurb.examples.kafka.security.restproxy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;

public abstract class RestProxyClient {

    protected static final String REST_PROXY_URL = "http://localhost:8082";

    protected static final MediaType JSON = MediaType.get("application/json");

    protected static final MediaType KAFKA_JSON_V2 = MediaType.get("application/vnd.kafka.json.v2+json");

    protected static final MediaType KAFKA_V2 = MediaType.get("application/vnd.kafka.v2+json");

    protected ObjectMapper objectMapper;

    protected OkHttpClient client;

    public RestProxyClient() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        client = new OkHttpClient();
    }

    protected Response postRequest(String url, String body, String contentType) throws IOException {
        RequestBody requestBody = RequestBody.create(body, KAFKA_JSON_V2);

        Request request = new Request.Builder()
                .post(requestBody)
                .header("Accept", contentType)
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                GenericResponse sendResponse = objectMapper.readValue(response.body().string(), GenericResponse.class);
                throw new IOException("Error with request: " + sendResponse);
            }

            return response;
        }
    }

    protected Response deleteRequest(String url) throws IOException {

        Request request = new Request.Builder()
                .delete()
                .header("Accept", KAFKA_V2.toString())
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                GenericResponse sendResponse = objectMapper.readValue(response.body().string(), GenericResponse.class);
                throw new IOException("Error with request: " + sendResponse);
            }

            return response;
        }
    }
}
