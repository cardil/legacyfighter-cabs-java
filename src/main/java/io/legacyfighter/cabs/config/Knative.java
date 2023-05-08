package io.legacyfighter.cabs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
@ConfigurationProperties(prefix = "k")
public class Knative {

  @SuppressWarnings("FieldMayBeFinal")
  private String sink = "http://localhost:31111/";

  @SuppressWarnings("unused")
  void setSink(String sink) {
    this.sink = sink;
  }

  public URL getSink() {
    try {
      return new URL(sink);
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
