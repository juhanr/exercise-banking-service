package ee.juhanr.exercise.banking.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.authentication.PreemptiveBasicAuthScheme;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ActiveProfiles("integration-test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class IntegrationTestBase {

    static final RabbitMQContainer rabbitMqContainer;
    static final ObjectMapper mapper = new ObjectMapper();

    static {
        rabbitMqContainer = new RabbitMQContainer("rabbitmq:3-management");
        rabbitMqContainer.start();
    }

    @DynamicPropertySource
    static void rabbitMqProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMqContainer::getContainerIpAddress);
        registry.add("spring.rabbitmq.port", rabbitMqContainer::getAmqpPort);
    }

    @BeforeAll
    static void beforeAll(@LocalServerPort int localPort,
            @Value("${spring.security.user.name}") String authUserName,
            @Value("${spring.security.user.password}") String authPassword) {
        RestAssured.port = localPort;
        PreemptiveBasicAuthScheme authScheme = new PreemptiveBasicAuthScheme();
        authScheme.setUserName(authUserName);
        authScheme.setPassword(authPassword);
        RestAssured.authentication = authScheme;

        assertThat(rabbitMqContainer.isRunning()).isTrue();
    }

    @SneakyThrows
    protected String asJson(Object o) {
        return mapper.writeValueAsString(o);
    }
}
