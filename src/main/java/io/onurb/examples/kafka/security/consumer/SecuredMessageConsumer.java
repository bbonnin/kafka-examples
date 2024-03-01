package io.onurb.examples.kafka.security.consumer;

public class SecuredMessageConsumer extends AbstractConsumer {

    public static void main(String[] args) {
        SecuredMessageConsumer consumer = new SecuredMessageConsumer();
        try {
            consumer.init();
            consumer.consumeMessages("payments-no-schema");
        }
        finally {
            consumer.close();
        }
    }
}
