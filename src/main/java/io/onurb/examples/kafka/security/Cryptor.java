package io.onurb.examples.kafka.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;

/**
 * Only for demo purpose !!!!
 */
public class Cryptor {

    private static final IvParameterSpec ivspec = new IvParameterSpec(
            new byte[]{ 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 });

    private static final SecretKeyFactory factory;

    static {
        try {
            factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] encrypt(String values, String secret, String salt) {
        return encrypt(values.getBytes(StandardCharsets.UTF_8), secret, salt);
    }

    public static byte[] encrypt(byte[] values, String secret, String salt) {
        try {
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivspec);
            return Base64.getEncoder().encode(cipher.doFinal(values));
        } catch (Exception e) {
            System.err.println("Error while encrypting: " + e);
        }
        return null;
    }

    public static byte[] decrypt(String val, String secret, String salt) {
        return decrypt(val.getBytes(StandardCharsets.UTF_8), secret, salt);
    }

    public static byte[] decrypt(byte[] val, String secret, String salt) {
        try {
            KeySpec spec = new PBEKeySpec(secret.toCharArray(), salt.getBytes(), 65536, 256);
            SecretKey tmp = factory.generateSecret(spec);
            SecretKeySpec secretKey = new SecretKeySpec(tmp.getEncoded(), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
            return cipher.doFinal(Base64.getDecoder().decode(val));
        } catch (Exception e) {
            System.err.println("Error while decrypting: " + e);
        }
        return null;
    }
}
