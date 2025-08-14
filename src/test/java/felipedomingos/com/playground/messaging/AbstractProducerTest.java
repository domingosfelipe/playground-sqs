package felipedomingos.com.playground.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.BatchResultErrorEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageBatchResultEntry;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AbstractProducerTest {

	final String queue = "https://domain.com/playground";
	@Mock SqsAsyncClient sqsAsyncClient;
	TestableProducer producer;

	@BeforeEach
	void setup() {
		producer = new TestableProducer(queue, sqsAsyncClient);
	}

	@Test
	void testProduce_singleMessage() {
		var message = "message-1";
		var response = SendMessageResponse.builder()
			.messageId(UUID.randomUUID().toString())
			.build();
		var captor = ArgumentCaptor.forClass(SendMessageRequest.class);
		when(sqsAsyncClient.sendMessage(any(SendMessageRequest.class)))
			.thenReturn(CompletableFuture.completedFuture(response));
		producer.produce(message);
		verify(sqsAsyncClient).sendMessage(captor.capture());
		var actual = captor.getValue();
		assertEquals(queue, actual.queueUrl());
		assertEquals(message, actual.messageBody());
		assertEquals(Integer.valueOf(10), actual.delaySeconds());
		verifyNoMoreInteractions(sqsAsyncClient);
	}

	@Test
	void testProduce_listOfMessage() {
		var message1 = "message-1";
		var message2 = "message-2";
		var messageErr = "message-err";
		var entry1 = SendMessageBatchResultEntry.builder()
			.id("id-1").messageId("m-1").md5OfMessageBody("md5-m1").build();
		var entry2 = SendMessageBatchResultEntry.builder()
			.id("id-2").messageId("m-2").md5OfMessageBody("md5-m2").build();
		var entry3 = BatchResultErrorEntry.builder()
			.id("id-3").message("m-3-bad").senderFault(true).code("500").build();
		var response = SendMessageBatchResponse.builder()
			.successful(entry1, entry2)
			.failed(entry3)
			.build();
		var captor = ArgumentCaptor.forClass(SendMessageBatchRequest.class);
		when(sqsAsyncClient.sendMessageBatch(any(SendMessageBatchRequest.class)))
			.thenReturn(CompletableFuture.completedFuture(response));
		producer.produce(List.of(message1, message2, messageErr));
		verify(sqsAsyncClient).sendMessageBatch(captor.capture());
		var actual = captor.getValue();
		assertEquals(queue, actual.queueUrl());
		assertEquals(3, actual.entries().size());
		var bodies = actual.entries().stream().map(SendMessageBatchRequestEntry::messageBody).toList();
		assertThat(bodies).containsExactlyInAnyOrder(message1, message2, messageErr);
		actual.entries().forEach(e -> assertEquals(Integer.valueOf(10), e.delaySeconds()));
		verifyNoMoreInteractions(sqsAsyncClient);
	}

	static class TestableProducer extends AbstractProducer {
		TestableProducer(String queue, SqsAsyncClient client) {super(queue, client);}

		public void produce(String message) {super.produce(message);}

		public void produce(List<String> messages) {super.produce(messages);}
	}
}
