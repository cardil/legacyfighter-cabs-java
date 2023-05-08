package io.legacyfighter.cabs.driverfleet;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.Objects;

@Entity
class Occupied {
  @Id
  Long driverId;

  public Occupied() {
  }

  public Occupied(Long driverId) {
    this.driverId = driverId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Occupied occupied = (Occupied) o;
    return Objects.equals(driverId, occupied.driverId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(driverId);
  }

}
