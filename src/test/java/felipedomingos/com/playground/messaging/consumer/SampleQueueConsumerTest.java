package felipedomingos.com.playground.messaging.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class SampleQueueConsumerTest {

	@Test
	void testConsume() {
		var queue = "https://domain.com/playground";
		var sqsAsyncClient = mock(SqsAsyncClient.class);
		var spy = spy(new SampleQueueConsumer(queue, sqsAsyncClient));
		doNothing().when(spy).consume();
		spy.consume();
		verifyNoMoreInteractions(spy);
	}

	@Test
	void consume_hasScheduledFixedRateDe20000Ms() throws NoSuchMethodException {
		var method = SampleQueueConsumer.class.getMethod("consume");
		var scheduled = method.getAnnotation(Scheduled.class);
		assertNotNull(scheduled, "@Scheduled annotation missing");
		assertEquals(20000L, scheduled.fixedRate(), "fixedRate invalid");
	}
}
