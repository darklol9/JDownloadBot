package org.darklol9.bots.utils;

import lombok.SneakyThrows;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.spec.KeySpec;

public class CryptUtil {

    private static SecretKey key;

    @SneakyThrows
    public static void init(char[] password, byte[] salt) {
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(password, salt, 65536, 256);
        key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");
    }

    @SneakyThrows
    public static String aes(String input) {
        byte[] bytes = input.getBytes();
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        bytes = cipher.doFinal(bytes);
        return Base64.encodeBase64String(bytes);
    }

}
