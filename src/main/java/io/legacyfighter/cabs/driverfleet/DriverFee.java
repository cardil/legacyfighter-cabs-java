package io.legacyfighter.cabs.driverfleet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cloudevents.CloudEvent;
import io.cloudevents.CloudEventContext;
import io.legacyfighter.cabs.common.Event;
import io.legacyfighter.cabs.common.cloudevents.From;
import io.legacyfighter.cabs.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Objects;

public class DriverFee implements Event {
  public final Data data;
  public final CloudEventContext ctx;

  DriverFee(Data data, CloudEventContext ctx) {
    this.data = data;
    this.ctx = ctx;
  }

  public static class Data {
    public final Long driverId;
    public final Money fee;

    @JsonCreator
    Data(@JsonProperty("driver-id") Long driverId, @JsonProperty Money fee) {
      this.driverId = driverId;
      this.fee = fee;
    }
  }

  @Service
  static class Converter implements From<DriverFee> {

    private final ObjectMapper objectMapper;

    @Autowired
    Converter(ObjectMapper objectMapper) {
      this.objectMapper = objectMapper;
    }

    @Override
    public boolean matches(CloudEvent event) {
      return "cabs.drivers.driver-fee".equals(event.getType());
    }

    @Override
    public DriverFee fromCloudEvent(CloudEvent event) {
      Objects.requireNonNull(event.getData());
      try {
        Data data = objectMapper.readValue(event.getData().toBytes(), Data.class);
        return new DriverFee(data, event);
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }
}
