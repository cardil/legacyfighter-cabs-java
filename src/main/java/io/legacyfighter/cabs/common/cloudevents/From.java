package io.legacyfighter.cabs.common.cloudevents;

import io.cloudevents.CloudEvent;
import io.legacyfighter.cabs.common.Event;

public interface From<T extends Event> {
  boolean matches(CloudEvent event);

  T fromCloudEvent(CloudEvent event);
}
