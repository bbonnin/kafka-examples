package io.onurb.examples.kafka.security.producer;

import java.io.IOException;

public class SecuredMessageProducer extends AbstractProducer {

    public static void main(String[] args) throws IOException {
        final SecuredMessageProducer producer = new SecuredMessageProducer();
        try {
            producer.init();
            producer.sendMessages("payments-no-schema", 10);
        }
        finally {
            producer.close();
        }
    }
}
