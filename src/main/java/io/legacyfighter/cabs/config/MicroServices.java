package io.legacyfighter.cabs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@ConfigurationProperties(prefix = "usvc")
public class MicroServices {
  private String drivers = "http://localhost:8081/drivers";

  @SuppressWarnings("unused")
  void setDrivers(String drivers) {
    this.drivers = drivers;
  }

  public URL getDrivers() {
    try {
      return new URL(drivers);
    } catch (MalformedURLException ex) {
      throw new UncheckedIOException(ex);
    }
  }
}
