package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.driverfleet.DriverDTO;
import io.legacyfighter.cabs.driverfleet.DriverDTO.Status;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
class DriverFixture {

  DriverDTO aDriver() {
    return aDriver(Status.ACTIVE, "Janusz", "Kowalsi", "FARME100165AB5EW");
  }

  DriverDTO aDriver(Status status, String name, String lastName, String driverLicense) {
    DriverDTO dto = new DriverDTO(
        name, lastName, new DriverDTO.License(driverLicense, null),
        "", status, DriverDTO.Type.REGULAR, Map.of()
    );
    dto.id = 420L;
    return dto;
  }
}
