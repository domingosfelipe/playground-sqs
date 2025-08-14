package felipedomingos.com.playground;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class AppTest extends LocalStackContainerConfig {

  @Test
  void contextLoads() {}
}