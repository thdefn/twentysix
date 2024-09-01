package cm.twentysix.user;

import cm.twentysix.user.domain.model.User;
import cm.twentysix.user.domain.model.UserType;
import cm.twentysix.user.domain.repository.UserRepository;
import cm.twentysix.user.util.CipherManager;
import net.andreinc.mockneat.MockNeat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@DisabledIfEnvironmentVariable(named = "SPRING_PROFILES_ACTIVE", matches = "ci")
class UserApplicationTests {

    private final MockNeat mockNeat = MockNeat.threadLocal();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CipherManager cipherManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {

    }

    private void addMockUsers(int size) {
        List<User> mockUsers = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            mockUsers.add(new User(
                    cipherManager.encrypt(mockNeat.emails().val()),
                    mockNeat.passwords().val(),
                    cipherManager.encrypt(mockNeat.names().val()),
                    passwordEncoder.encode(mockNeat.passwords().val()),
                    UserType.CUSTOMER
            ));
            System.out.println(i + " : " + mockUsers.getLast());
        }

        userRepository.saveAll(mockUsers);
        System.out.println("completed");
    }

}
