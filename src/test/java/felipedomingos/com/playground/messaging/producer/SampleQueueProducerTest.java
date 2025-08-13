package felipedomingos.com.playground.messaging.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SampleQueueProducerTest {

  @Test
  void testProduce_singleMessage() {
    var queue = "https://domain.com/playground";
    var message = "message-1";
    var sqsAsyncClient = mock(SqsAsyncClient.class);
    var spy = spy(new SampleQueueProducer(queue, sqsAsyncClient));
    doNothing().when(spy).produce(message);
    spy.produce(message);
    verifyNoMoreInteractions(spy);
  }

  @Test
  void testProduce_listOfMessages() {
    var queue = "https://domain.com/playground";
    var message1 = "message-1";
    var message2 = "message-2";
    var messages = List.of(message1, message2);
    var sqsAsyncClient = mock(SqsAsyncClient.class);
    var spy = spy(new SampleQueueProducer(queue, sqsAsyncClient));
    doNothing().when(spy).produce(messages);
    spy.produce(messages);
    verifyNoMoreInteractions(spy);
  }
}