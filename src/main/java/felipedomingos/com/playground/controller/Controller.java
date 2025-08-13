package felipedomingos.com.playground.controller;

import felipedomingos.com.playground.messaging.producer.SampleQueueProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1")
public class Controller {

  private final SampleQueueProducer producer;

  public Controller(SampleQueueProducer producer) {
    this.producer = producer;
  }

  @PostMapping("/message")
  public ResponseEntity<Void> post(@RequestBody String message) {
    producer.produce(message);
    return ResponseEntity.accepted().build();
  }

  @PostMapping("/messages")
  public ResponseEntity<Void> post(@RequestBody List<String> messages) {
    producer.produce(messages);
    return ResponseEntity.accepted().build();
  }
}
