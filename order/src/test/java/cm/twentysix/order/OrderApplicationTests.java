package cm.twentysix.order;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "CI", matches = "false")
class OrderApplicationTests {

    @Test
    void contextLoads() {
    }

}
