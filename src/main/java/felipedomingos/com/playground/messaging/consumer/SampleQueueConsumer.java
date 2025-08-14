package felipedomingos.com.playground.messaging.consumer;

import felipedomingos.com.playground.messaging.AbstractConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

@Component
public class SampleQueueConsumer extends AbstractConsumer {

	public SampleQueueConsumer(@Value("${sqs.queues.sample}") String queue, SqsAsyncClient sqsAsyncClient) {
		super(queue, sqsAsyncClient);
	}

	@Scheduled(fixedRate = 20000)
	public void consume() {
		super.consume();
	}
}
