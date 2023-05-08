package io.legacyfighter.cabs.driverfleet;

import io.legacyfighter.cabs.common.cloudevents.Publisher;
import io.legacyfighter.cabs.money.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DriverFeeService {

    private final Publisher publisher;

    @Autowired
    public DriverFeeService(Publisher publisher) {
        this.publisher = publisher;
    }

    public void calculateDriverFee(UUID rideId, Money transitPrice, Long driverId) {
        publisher.publish(new CalculateFee(
            rideId,
            driverId,
            transitPrice.toInt()
        ));
    }
}
