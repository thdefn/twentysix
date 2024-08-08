package cm.twentysix.user.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class EncryptUtilTest {

    private EncryptUtil encryptUtil = new EncryptUtil("abcdefghijklmnopqrstuvwxyzabcdef");

    EncryptUtilTest() throws Exception {
    }

    @BeforeEach
    void init() {

    }

    @Test
    @DisplayName("양방향 암호화 성공")
    void twoWayEncrypt_success() throws Exception {
        //given
        String plain = "김보영은 인간의 경험에 대해 장르를 바꾸는 시각을 제공한다.";
        //when
        String cipher = encryptUtil.encrypt(plain);
        //then
        String decrypted = encryptUtil.decrypt(cipher);
        assertNotEquals(plain, cipher);
        assertEquals(decrypted, plain);
    }

}