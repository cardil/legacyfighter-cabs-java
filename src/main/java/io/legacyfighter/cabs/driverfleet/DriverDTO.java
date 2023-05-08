package io.legacyfighter.cabs.driverfleet;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.OffsetDateTime;
import java.util.Map;

public class DriverDTO {
  public Long id;
  public final String name;
  public final String surname;
  public final License license;
  public final String photo;
  public final Status status;
  public final Type type;
  public final Map<String, String> attributes;

  @JsonCreator
  public DriverDTO(
      @JsonProperty String name,
      @JsonProperty String surname,
      @JsonProperty License license,
      @JsonProperty String photo,
      @JsonProperty Status status,
      @JsonProperty Type type,
      @JsonProperty Map<String, String> attributes
  ) {
    this.name = name;
    this.surname = surname;
    this.license = license;
    this.photo = photo;
    this.status = status;
    this.type = type;
    this.attributes = attributes;
  }

  public enum Status {
    ACTIVE("Active"),
    INACTIVE("Inactive");

    private final String repr;

    Status(String repr) {
      this.repr = repr;
    }

    @JsonValue
    @Override
    public String toString() {
      return repr;
    }
  }

  public enum Type {
    CANDIDATE("Candidate"),
    REGULAR("Regular");

    private final String repr;

    Type(String repr) {
      this.repr = repr;
    }

    @JsonValue
    @Override
    public String toString() {
      return repr;
    }
  }

  public static final class License {
    public final String number;
    public final OffsetDateTime expires;

    @JsonCreator
    public License(
      @JsonProperty String number,
      @JsonProperty OffsetDateTime expires
    ) {
      this.number = number;
      this.expires = expires;
    }
  }
}
