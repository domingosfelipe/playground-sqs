package felipedomingos.com.playground.messaging.consumer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.annotation.Scheduled;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SampleQueueConsumerTest {

  @Test
  void testPoll() {
    var queue = "https://domain.com/playground";
    var sqsAsyncClient = mock(SqsAsyncClient.class);
    var spy = spy(new SampleQueueConsumer(queue, sqsAsyncClient));
    doNothing().when(spy).poll();
    spy.poll();
    verifyNoMoreInteractions(spy);
  }

  @Test
  void poll_temScheduledFixedRateDe20000ms() throws NoSuchMethodException {
    var method = SampleQueueConsumer.class.getMethod("poll");
    var scheduled = method.getAnnotation(Scheduled.class);
    assertNotNull(scheduled, "@Scheduled annotation missing");
    assertEquals(20000L, scheduled.fixedRate(), "fixedRate invalid");
  }
}