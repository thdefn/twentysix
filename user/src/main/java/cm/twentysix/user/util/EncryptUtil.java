package cm.twentysix.user.util;

import org.springframework.beans.factory.annotation.Value;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptUtil {

    private final static String algorithm = "AES/CBC/PKCS5Padding";
    private final SecretKey secretKey;
    private final Cipher cipher;
    private final IvParameterSpec ivParameterSpec;

    public EncryptUtil(@Value("${encrypt.key}") String key) throws Exception {
        secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        cipher = Cipher.getInstance(algorithm);
        byte[] initialVector = new byte[16];
        new SecureRandom().nextBytes(initialVector);
        ivParameterSpec = new IvParameterSpec(initialVector);
    }

    public String encrypt(String plain) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
        byte[] cryptogram = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        return new String(Base64.getEncoder().encode(cryptogram)).trim();
    }

    public String decrypt(String crypto) throws Exception {
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
        byte[] plain = cipher.doFinal(Base64.getDecoder().decode(crypto.getBytes()));
        return new String(plain).trim();
    }
}
