package io.legacyfighter.cabs.common.cloudevents;

import io.cloudevents.CloudEvent;

import java.lang.reflect.ParameterizedType;

public interface Into<T> {
  CloudEvent into(T event);

  default boolean accepts(Object event) {
    return getEventType().isInstance(event);
  }

  default Class<?> getEventType() {
    return (Class<?>) ((ParameterizedType) getClass()
        .getGenericInterfaces()[0])
        .getActualTypeArguments()[0];
  }
}
