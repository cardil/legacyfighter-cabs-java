package io.legacyfighter.cabs.common.cloudevents;


import io.cloudevents.CloudEvent;
import io.legacyfighter.cabs.common.Event;
import io.legacyfighter.cabs.common.EventsPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Receiver {
  private static final Logger log = LoggerFactory.getLogger(Receiver.class);

  private final EventsPublisher eventsPublisher;
  private final List<From<?>> froms;

  @Autowired
  Receiver(EventsPublisher eventsPublisher, List<From<?>> froms) {
    this.eventsPublisher = eventsPublisher;
    this.froms = froms;
  }

  @PostMapping("/")
  public void receive(@RequestBody CloudEvent event) {
    log.info("Received event: {}", event);

    for (From<?> from : froms) {
      if (from.matches(event)) {
        Event ev = from.fromCloudEvent(event);
        eventsPublisher.publish(ev);
        return;
      }
    }

    throw new IllegalStateException("No matching event type consumer found");
  }
}
