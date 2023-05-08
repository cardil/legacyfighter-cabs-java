package io.legacyfighter.cabs.common.cloudevents;

import io.cloudevents.CloudEvent;
import io.cloudevents.http.HttpMessageFactory;
import io.cloudevents.http.impl.HttpMessageWriter;
import io.legacyfighter.cabs.config.Knative;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Service
public class Publisher {

  private static final Logger log = LoggerFactory.getLogger(Publisher.class);

  private final Knative knative;
  private final List<Into<?>> converters;

  @Autowired
  Publisher(Knative knative, List<Into<?>> converters) {
    this.knative = knative;
    this.converters = converters;
  }

  public void publish(Object event) {
    try {
      unsafePublish(event);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private <T> void unsafePublish(T event) throws IOException {
    @SuppressWarnings("unchecked")
    Into<T> convert = (Into<T>) converters.stream()
        .filter(c -> c.accepts(event))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException(
          "Cannot find converter for " + event.getClass()));
    CloudEvent ce = convert.into(event);
    URL url = knative.getSink();
    log.info("Publishing event to {} : {}", url, ce);
    HttpURLConnection http = (HttpURLConnection) url.openConnection();
    http.setRequestMethod("POST");
    http.setDoOutput(true);
    http.setDoInput(true);

    HttpMessageWriter messageWriter = createMessageWriter(http);
    messageWriter.writeBinary(ce);

    int code = http.getResponseCode();
    if (code < 200 || code >= 300) {
      throw new IOException("Unexpected response code " + code);
    }
  }

  private static HttpMessageWriter createMessageWriter(HttpURLConnection httpUrlConnection) {
    return HttpMessageFactory.createWriter(httpUrlConnection::setRequestProperty, body -> {
      try {
        if (body != null) {
          httpUrlConnection.setRequestProperty("content-length", String.valueOf(body.length));
          try (OutputStream outputStream = httpUrlConnection.getOutputStream()) {
            outputStream.write(body);
          }
        } else {
          httpUrlConnection.setRequestProperty("content-length", "0");
        }
      } catch (IOException t) {
        throw new UncheckedIOException(t);
      }
    });
  }
}
