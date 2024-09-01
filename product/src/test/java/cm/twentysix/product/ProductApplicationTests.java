package cm.twentysix.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnabledIfEnvironmentVariable(named = "CI", matches = "false")
class ProductApplicationTests {

    @Test
    void contextLoads() {
    }

}
