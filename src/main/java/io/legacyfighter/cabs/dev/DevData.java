package io.legacyfighter.cabs.dev;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.carfleet.CarTypeDTO;
import io.legacyfighter.cabs.carfleet.CarTypeService;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.crm.ClientDTO;
import io.legacyfighter.cabs.crm.ClientService;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.ride.RequestTransitService;
import io.legacyfighter.cabs.ride.RideService;
import io.legacyfighter.cabs.ride.TransitDTO;
import io.legacyfighter.cabs.tracking.DriverSessionService;
import io.legacyfighter.cabs.tracking.DriverTrackingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.IntStream;

@Component
@Profile("dev")
class DevData {
  private static final Logger log = LoggerFactory.getLogger(DevData.class);

  @Autowired
  private RideService rideService;
  @Autowired
  private ClientService clientService;
  @Autowired
  private DriverTrackingService trackingService;
  @Autowired
  private CarTypeService carTypeService;
  @Autowired
  private DriverSessionService driverSessionService;
  @Autowired
  private RequestTransitService requestTransitService;
  @Autowired
  private GeocodingService geocodingService;
  @Autowired
  private ObjectMapper objectMapper;

  @EventListener
  public void init(ApplicationStartedEvent ignored) throws JsonProcessingException {
    log.info("Deploying dev data for ride module");

    anActiveCarCategory(CarClass.ECO);
    anActiveCarCategory(CarClass.REGULAR);

    AddressDTO pickup = new AddressDTO("Polska", "Warszawa",
      "Młynarska", 20);
    AddressDTO destination = new AddressDTO("Polska", "Warszawa",
      "Żytnia", 32);

    Client client = clientService.registerClient("John", "Doe",
      Client.Type.NORMAL, Client.PaymentType.POST_PAID);

    long driverId = 1993432552L; // Rob Falc
    driverSessionService.logIn(driverId, "WZ 2133T",
      CarClass.REGULAR, "Toyota Prius");
    double[] loc = geocodingService.geocodeAddress(pickup.toAddressEntity());
    trackingService.registerPosition(driverId,
      loc[0], loc[1], Instant.now());

    TransitDTO transit = new TransitDTO();
    transit.setClientDTO(new ClientDTO(client));
    transit.setFrom(pickup);
    transit.setTo(destination);
    transit = rideService.createTransit(transit);

    rideService.publishTransit(transit.getRequestId());
    rideService.acceptTransit(driverId, transit.getRequestId());
    rideService.startTransit(transit.getRequestId());
    if ("true".equals(System.getenv("DEV_DATA_COMPLETE_TRANSIT"))) {
      rideService.completeTransit(transit.getRequestId(), transit.getTo());
    } else {
      Long id = requestTransitService.findRequestId(transit.getRequestId());
      String payload = objectMapper.writeValueAsString(transit.getTo());
      log.info("Complete the transit ({}) by calling: /transits/{}/complete\n\n{}",
        transit.getRequestId(), id, payload);
    }
  }

  void anActiveCarCategory(CarClass carClass) {
    CarTypeDTO carTypeDTO = new CarTypeDTO();
    carTypeDTO.setCarClass(carClass);
    carTypeDTO.setDescription("opis");
    CarTypeDTO carType = carTypeService.create(carTypeDTO);
    IntStream.range(1, carType.getMinNoOfCarsToActivateClass() + 1)
      .forEach(i -> carTypeService.registerCar(carType.getCarClass()));
    carTypeService.activate(carType.getId());
  }
}
