package io.onurb.examples.kafka.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.onurb.examples.kafka.security.annotation.EncryptedData;
import io.onurb.examples.kafka.security.annotation.EncryptedField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * In real life, either you use EncryptData or EncryptField, not both.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
//@EncryptedData
public class Payment {

    @JsonProperty(required = true)
    private double amount;

    @JsonProperty(required = true)
    private String name;

    @EncryptedField
    @JsonProperty(required = true)
    private String iban;
}
