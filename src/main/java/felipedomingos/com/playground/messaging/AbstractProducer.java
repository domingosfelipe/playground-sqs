package felipedomingos.com.playground.messaging;

import org.slf4j.Logger;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.List;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;

public abstract class AbstractProducer {

	private final String queue;
	private final SqsAsyncClient sqsAsyncClient;
	private final Logger logger = getLogger(AbstractProducer.class);

	public AbstractProducer(String queue, SqsAsyncClient sqsAsyncClient) {
		this.queue = queue;
		this.sqsAsyncClient = sqsAsyncClient;
	}

	protected void produce(String message) {
		var sendRequest = SendMessageRequest.builder()
			.queueUrl(queue)
			.messageBody(message)
			.delaySeconds(10)
			.build();

		sqsAsyncClient.sendMessage(sendRequest)
			.whenComplete((response, throwable) -> {
				if (throwable != null) {
					logger.error("Queue: {} error while sending messages: {}", queue, throwable.getMessage(), throwable);
					return;
				}
				logger.info("Queue: {} message has been sent: {} / body: {}", queue, response.messageId(), message);
			});
	}

	protected void produce(List<String> messages) {
		var entries = messages.stream()
			.map(msg -> SendMessageBatchRequestEntry.builder()
				.id(UUID.randomUUID().toString())
				.messageBody(msg)
				.delaySeconds(10)
				.build())
			.toList();

		var sendBatchRequest = SendMessageBatchRequest.builder()
			.queueUrl(queue)
			.entries(entries)
			.build();

		sqsAsyncClient.sendMessageBatch(sendBatchRequest)
			.whenComplete((response, throwable) -> {
				if (throwable != null) {
					logger.error("Queue: {} error while sending messages: {}", queue, throwable.getMessage(), throwable);
					return;
				}
				response.failed().forEach(e -> {
					logger.info("Queue: {} message has failed to sent: {} / body: {} / code: {}", queue, e.id(), e.message(), e.code());
				});
				response.successful().forEach(e -> {
					logger.info("Queue: {} message has been sent: {} / body(md5): {}", queue, e.messageId(), e.md5OfMessageBody());
				});
			});
	}
}
