package felipedomingos.com.playground.messaging;

import org.slf4j.Logger;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractConsumer {

  private final String queue;
  private final SqsAsyncClient sqsAsyncClient;
  private final Logger logger = getLogger(AbstractConsumer.class);

  public AbstractConsumer(String queue, SqsAsyncClient sqsAsyncClient) {
    this.queue = queue;
    this.sqsAsyncClient = sqsAsyncClient;
  }

  protected void consume() {
    var receiveRequest = ReceiveMessageRequest.builder()
        .queueUrl(queue)
        .maxNumberOfMessages(10)
        .waitTimeSeconds(20)
        .visibilityTimeout(30)
        .build();

    sqsAsyncClient.receiveMessage(receiveRequest)
        .whenComplete((response, throwable) -> {
          if (throwable != null) {
            logger.error("Queue: {} error while receiving messages: {}", queue, throwable.getMessage(), throwable);
            return;
          }

          if (response.hasMessages() == false) {
            logger.warn("Queue: {} no messages to receive!", queue);
            return;
          }

          response.messages().forEach(msg -> {
            logger.info("Queue: {} message received: {} / body: {}", queue, msg.messageId(), msg.body());
            var deleteRequest = DeleteMessageRequest.builder().queueUrl(queue).receiptHandle(msg.receiptHandle()).build();
            sqsAsyncClient.deleteMessage(deleteRequest).join();
          });
        });
  }
}
