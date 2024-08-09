package cm.twentysix.user.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class CipherManager {

    private final static String algorithm = "AES/CBC/PKCS5Padding";
    private final SecretKey secretKey;
    private final Cipher cipher;
    private final IvParameterSpec ivParameterSpec;

    public CipherManager(@Value("${encrypt.key}") String key) throws Exception {
        secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        cipher = Cipher.getInstance(algorithm);
        ivParameterSpec = new IvParameterSpec(key.substring(0, 16).getBytes(StandardCharsets.UTF_8));
    }

    public String encrypt(String plain) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
            byte[] cryptogram = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
            return new String(Base64.getEncoder().encode(cryptogram)).trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public String decrypt(String crypto) {
        try {
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] plain = cipher.doFinal(Base64.getDecoder().decode(crypto.getBytes()));
            return new String(plain).trim();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
