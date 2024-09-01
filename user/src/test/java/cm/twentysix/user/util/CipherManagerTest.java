package cm.twentysix.user.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.IfProfileValue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest(properties = "encrypt.key=abcdefghijklmnopqrstuvwxyzabcdef")
@DisabledIfEnvironmentVariable(named = "SPRING_PROFILES_ACTIVE", matches = "ci")
class CipherManagerTest {

    @Autowired
    private CipherManager cipherManager;

    @Test
    @DisplayName("양방향 암호화 성공")
    void twoWayEncrypt_success() {
        //given
        String plain = "김보영은 인간의 경험에 대해 장르를 바꾸는 시각을 제공한다.";
        //when
        String cipher = cipherManager.encrypt(plain);
        //then
        String decrypted = cipherManager.decrypt(cipher);
        assertNotEquals(plain, cipher);
        assertEquals(decrypted, plain);
    }

}