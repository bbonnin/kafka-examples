package io.onurb.examples.kafka.security;

import java.util.Map;

import static io.onurb.examples.kafka.security.Constants.DEMO_ENCRYPTED_FIELD_KEY_NAME;

/***
 * Simulates a Key Management Service, where we retrieve keys for encrypting/decrypting data.
 */
public final class KMS {

    private static Map<String, String> keys = Map.of(
            DEMO_ENCRYPTED_FIELD_KEY_NAME, "TheDemoKey");

    public static String getKey(String keyName) {
        return keys.get(keyName);
    }
}
