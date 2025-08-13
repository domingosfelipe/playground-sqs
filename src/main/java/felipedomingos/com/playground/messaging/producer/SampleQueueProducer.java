package felipedomingos.com.playground.messaging.producer;

import felipedomingos.com.playground.messaging.AbstractProducer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.List;

@Component
public class SampleQueueProducer extends AbstractProducer {

  public SampleQueueProducer(@Value("${sqs.queues.sample}") String queue, SqsAsyncClient sqsAsyncClient) {
    super(queue, sqsAsyncClient);
  }

  public void produce(String message) {
    super.produce(message);
  }

  public void produce(List<String> messages) {
    super.produce(messages);
  }
}
