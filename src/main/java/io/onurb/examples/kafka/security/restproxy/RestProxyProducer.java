package io.onurb.examples.kafka.security.restproxy;

import com.github.javafaker.Faker;
import io.onurb.examples.kafka.security.Payment;
import okhttp3.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class RestProxyProducer extends RestProxyClient {

    public static void main(String[] args) throws IOException {
        RestProxyProducer consumer = new RestProxyProducer();
        consumer.sendMessages("payments-rest-proxy", 1);
    }

    protected void sendMessages(String topic, int nb) throws IOException {
        final Faker faker = new Faker();

        for (int i = 0; i < nb; i++) {
            Payment payment = new Payment(
                    faker.number().randomDouble(2, 1, 1_000_000),
                    faker.name().fullName(),
                    faker.finance().iban().toUpperCase());

            System.out.println("Send new payment: " + payment);
            sendMessage(topic, payment);
        }
    }

    private void sendMessage(String topic, Payment payment) throws IOException {
        payment.setIban(Base64.getEncoder().encodeToString(payment.getIban().getBytes(StandardCharsets.UTF_8))); // Very strong encryption ;)
        Records<Payment> records = new Records<>();
        records.records.add(new Record<>(payment));
        String json = objectMapper.writeValueAsString(records);
        RequestBody body = RequestBody.create(json, KAFKA_JSON_V2);
        Request request = new Request.Builder()
                .post(body)
                .header("Content-Type", KAFKA_JSON_V2.toString())
                .url(REST_PROXY_URL + "/topics/" + topic)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                GenericResponse sendResponse = objectMapper.readValue(response.body().string(), GenericResponse.class);
                System.err.println("Erreur lors de l'envoi: " + sendResponse.code + "/" + sendResponse.message);
            }
        }
    }
}
