package felipedomingos.com.playground.messaging;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AbstractConsumerTest {

  @Mock SqsAsyncClient sqsAsyncClient;

  final String queue = "https://domain.com/playground";
  TestableConsumer consumer;

  static class TestableConsumer extends AbstractConsumer<String> {
    TestableConsumer(String queue, SqsAsyncClient client) { super(queue, client); }
    void poll() { consume(); }
  }

  @BeforeEach
  void setUp() {
    consumer = new TestableConsumer(queue, sqsAsyncClient);
  }

  @Test
  void testConsume_handlingError() {
    when(sqsAsyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
        .thenReturn(CompletableFuture.failedFuture(new RuntimeException("exception")));
    consumer.poll();
    verifyNoMoreInteractions(sqsAsyncClient);
  }

  @Test
  void testConsume_handlingEmptyMessages() {
    var response = ReceiveMessageResponse.builder()
        .messages(List.of())
        .build();
    when(sqsAsyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(response));
    consumer.poll();
    verifyNoMoreInteractions(sqsAsyncClient);
  }

  @Test
  void testConsume() {
    var msg1 = Message.builder().messageId("m-1").receiptHandle("rh-1").body("body-1").build();
    var msg2 = Message.builder().messageId("m-2").receiptHandle("rh-2").body("body-2").build();
    var response = ReceiveMessageResponse.builder().messages(msg1, msg2).build();
    when(sqsAsyncClient.receiveMessage(any(ReceiveMessageRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(response));
    when(sqsAsyncClient.deleteMessage(any(DeleteMessageRequest.class)))
        .thenReturn(CompletableFuture.completedFuture(DeleteMessageResponse.builder().build()));
    consumer.poll();

    var receiveCaptor = ArgumentCaptor.forClass(ReceiveMessageRequest.class);
    verify(sqsAsyncClient).receiveMessage(receiveCaptor.capture());
    var actual = receiveCaptor.getValue();
    assertEquals(queue, actual.queueUrl());
    assertEquals(Integer.valueOf(10), actual.maxNumberOfMessages());
    assertEquals(Integer.valueOf(20), actual.waitTimeSeconds());
    assertEquals(Integer.valueOf(30), actual.visibilityTimeout());

    var deleteCaptor = ArgumentCaptor.forClass(DeleteMessageRequest.class);
    verify(sqsAsyncClient, times(2)).deleteMessage(deleteCaptor.capture());
    assertEquals("rh-1", deleteCaptor.getAllValues().get(0).receiptHandle());
    assertEquals(queue,  deleteCaptor.getAllValues().get(0).queueUrl());
    assertEquals("rh-2", deleteCaptor.getAllValues().get(1).receiptHandle());
    assertEquals(queue,  deleteCaptor.getAllValues().get(1).queueUrl());

    verifyNoMoreInteractions(sqsAsyncClient);
  }
}