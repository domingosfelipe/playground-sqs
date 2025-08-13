package felipedomingos.com.playground.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
public class SqsConfig {

  @Value("${spring.cloud.aws.credentials.access-key}")
  private String accessKey;
  @Value("${spring.cloud.aws.credentials.secret-key}")
  private String secretKey;
  @Value("${spring.cloud.aws.sqs.endpoint}")
  private String endpoint;
  @Value("${spring.cloud.aws.sqs.region}")
  private String region;

  @Bean
  public SqsAsyncClient sqsAsyncClient() {
    var sqsAsyncClient = SqsAsyncClient.builder();
    if (endpoint != null) {
      sqsAsyncClient.endpointOverride(URI.create(endpoint));
    }
    sqsAsyncClient.credentialsProvider(() -> AwsBasicCredentials.create(accessKey, secretKey));
    sqsAsyncClient.region(Region.of(region));
    return sqsAsyncClient.build();
  }
}
