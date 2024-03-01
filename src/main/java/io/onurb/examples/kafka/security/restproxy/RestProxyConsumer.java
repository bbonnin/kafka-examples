package io.onurb.examples.kafka.security.restproxy;

import io.onurb.examples.kafka.security.Payment;
import okhttp3.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;

public class RestProxyConsumer extends RestProxyClient {

    private static final String CONSUMER_GROUP = "payments-group";

    public static void main(String[] args) throws IOException, InterruptedException {
        RestProxyConsumer consumer = new RestProxyConsumer();
        consumer.consumeMessages("payments-rest-proxy");
    }

    private void consumeMessages(String topic) throws IOException, InterruptedException {

        try {
            // Creation consumer
            //
            postRequest(REST_PROXY_URL + "/consumers/" + CONSUMER_GROUP, """
                    {"name": "consumer-1", "format": "json", "auto.offset.reset": "earliest"}
                    """, KAFKA_V2.toString());

            // Subscribe to the topic
            //
            postRequest(REST_PROXY_URL + "/consumers/" + CONSUMER_GROUP + "/instances/consumer-1/subscription",
                    """
                    {"topics":["__"]}
                    """.replace("__", topic), KAFKA_V2.toString());

            // Consume messages
            //
            while (true) {
                Request request = new Request.Builder()
                        .get()
                        .header("Accept", KAFKA_JSON_V2.toString())
                        .url(REST_PROXY_URL + "/consumers/" + CONSUMER_GROUP + "/instances/consumer-1/records")
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        GenericResponse sendResponse = objectMapper.readValue(response.body().string(), GenericResponse.class);
                        throw new IOException("Error with request: " + sendResponse);
                    }
                    else {
                        //[{"topic":"payments-rest-proxy","key":null,"value":{"amount":362172.92,"name":"Lamar Flatley","iban":"SUU0OVVHS0oyNjM5NTA4NDk3MDkyOQ=="},"partition":0,"offset":9}]
                        String body = response.body().string();
                        PaymentMessage[] messages = objectMapper.readValue(body, PaymentMessage[].class);
                        Arrays.stream(messages).forEach(paymentMessage -> {
                            String encryptedIban = paymentMessage.value.getIban();
                            String decryptedIban = new String(Base64.getDecoder().decode(encryptedIban));
                            paymentMessage.value.setIban(decryptedIban);
                            System.out.println("New payment: " + paymentMessage.value);
                        });
                    }
                }

                Thread.sleep(100);
            }
        }
        finally {
            // Delete consumer
            //
            deleteRequest(REST_PROXY_URL + "/consumers/" + CONSUMER_GROUP + "/instances/consumer-1");
        }
    }

    static class PaymentMessage extends Message<Payment> {}
}
