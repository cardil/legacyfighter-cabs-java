package io.legacyfighter.cabs.driverfleet;

import io.legacyfighter.cabs.config.MicroServices;
import io.legacyfighter.cabs.money.Money;
import io.legacyfighter.cabs.ride.details.Status;
import io.legacyfighter.cabs.ride.details.TransitDetailsDTO;
import io.legacyfighter.cabs.ride.details.TransitDetailsFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@Service
public class DriverService {

    public static final String DRIVER_ID = "driver-id";
    @Autowired
    private TransitDetailsFacade transitDetailsFacade;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MicroServices microServices;

    @Autowired
    private OccupiedRepository driversOccupied;

    public Money calculateDriverMonthlyPayment(Long driverId, int year, int month) {
        DriverDTO driver = loadDriver(driverId);
        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exists, id = " + driverId);
        }
        YearMonth yearMonth = YearMonth.of(year, month);
        Instant from = yearMonth
                .atDay(1).atStartOfDay(ZoneId.systemDefault())

                .toInstant();
        Instant to = yearMonth

                .atEndOfMonth().plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

        List<TransitDetailsDTO> transitsList = transitDetailsFacade.findByDriver(driverId, from, to);

        return transitsList.stream()
                .filter(t -> Status.COMPLETED.equals(t.status))
                .filter(t -> t.driverFee != null && t.driverFee.toInt() > 0)
                .map(t -> t.driverFee)
                .reduce(Money.ZERO, Money::add);
    }

    public Map<Month, Money> calculateDriverYearlyPayment(Long driverId, int year) {
        Map<Month, Money> payments = new EnumMap<>(Month.class);
        for (Month m : Month.values()) {
            payments.put(m, calculateDriverMonthlyPayment(driverId, year, m.getValue()));
        }
        return payments;
    }

    public DriverDTO loadDriver(Long driverId) {
        String url = String.format("%s/{driver-id}", microServices.getDrivers());
        DriverDTO driver = restTemplate.getForObject(url,
            DriverDTO.class, Map.of(DRIVER_ID, driverId));
        if (driver == null) {
            throw new IllegalArgumentException("Driver does not exists, id = " + driverId);
        }
        driver.id = driverId;
        return driver;
    }

    public Set<DriverDTO> loadDrivers(Collection<Long> ids) {
        return ids
                .stream()
                .map(this::loadDriver)
                .collect(Collectors.toSet());
    }

    public boolean exists(Long driverId) {
        try {
            return loadDriver(driverId) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public void markOccupied(Long driverId) {
        driversOccupied.saveAndFlush(new Occupied(driverId));
    }

    public void markNotOccupied(Long driverId) {
        driversOccupied.deleteById(driverId);
    }

    public Predicate<DriverDTO> preloadOccupied(List<Long> driversIds) {
        List<Occupied> cache = driversOccupied.findAllById(driversIds);
        return driver -> cache.stream().anyMatch(
          o -> o.driverId.equals(driver.id)
        ) || driversOccupied.findById(driver.id).isPresent();
    }
}
