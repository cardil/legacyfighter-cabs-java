package io.legacyfighter.cabs.driverfleet;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.v1.CloudEventBuilder;
import io.legacyfighter.cabs.common.cloudevents.Into;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UncheckedIOException;
import java.net.URI;
import java.time.Clock;
import java.util.UUID;

class CalculateFee {
  private final UUID rideId;
  private final Data data;

  CalculateFee(UUID rideId, long driverId, int transitPrice) {
    this.rideId = rideId;
    this.data = new Data(driverId, transitPrice);
  }

  private static final class Data {
    @JsonProperty("driver-id")
    final long driverId;
    @JsonProperty("transit-price")
    final int transitPrice;

    private Data(long driverId, int transitPrice) {
      this.driverId = driverId;
      this.transitPrice = transitPrice;
    }
  }

  @Service
  static class Converter implements Into<CalculateFee> {
    @Autowired
    private ObjectMapper om;
    @Autowired
    private Clock clock;

    @Override
    public CloudEvent into(CalculateFee event) {
      try {
        return new CloudEventBuilder()
          .withId(UUID.randomUUID().toString())
          .withTime(clock.instant().atZone(clock.getZone()).toOffsetDateTime())
          .withSource(URI.create("usvc://cabs/legacy"))
          .withType("cabs.drivers.calculate-fee")
          .withDataContentType("application/json")
          .withData(om.writeValueAsBytes(event.data))
          .withSubject(event.rideId.toString())
          .build();
      } catch (JsonProcessingException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}
