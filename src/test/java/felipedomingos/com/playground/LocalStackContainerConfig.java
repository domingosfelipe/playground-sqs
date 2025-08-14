package felipedomingos.com.playground;

import org.junit.jupiter.api.BeforeAll;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;

public abstract class LocalStackContainerConfig {

	private static final LocalStackContainer LOCALSTACK;
	private static final String SAMPLE_QUEUE_NAME = "sample-queue";

	static {
		LOCALSTACK = new LocalStackContainer(DockerImageName.parse("localstack/localstack:latest"))
			.withServices(LocalStackContainer.Service.SQS)
			.withReuse(Boolean.TRUE)
			.withStartupAttempts(3)
			.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("localstack")))
			.waitingFor(Wait.forLogMessage(".*Ready\\.\n", 1));

		LOCALSTACK.start();
	}

	@BeforeAll
	static void setup() {
		var sqsClientBuilder = SqsClient.builder()
			.endpointOverride(LOCALSTACK.getEndpointOverride(LocalStackContainer.Service.SQS))
			.region(Region.of(LOCALSTACK.getRegion()))
			.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(LOCALSTACK.getAccessKey(), LOCALSTACK.getSecretKey())));

		try (var sqsClient = sqsClientBuilder.build()) {
			var createQueueRequest = CreateQueueRequest.builder().queueName(SAMPLE_QUEUE_NAME).build();
			sqsClient.createQueue(createQueueRequest);
		}
	}

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.cloud.aws.region.static", LOCALSTACK::getRegion);
		registry.add("spring.cloud.aws.sqs.endpoint", () -> LOCALSTACK.getEndpointOverride(LocalStackContainer.Service.SQS).toString());
		registry.add("spring.cloud.aws.sqs.region", LOCALSTACK::getRegion);
		registry.add("spring.cloud.aws.credentials.access-key", LOCALSTACK::getAccessKey);
		registry.add("spring.cloud.aws.credentials.secret-key", LOCALSTACK::getSecretKey);
		registry.add("sqs.queues.sample", () -> SAMPLE_QUEUE_NAME);
	}
}
