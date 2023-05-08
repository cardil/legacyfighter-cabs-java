package io.legacyfighter.cabs.common;


import io.legacyfighter.cabs.carfleet.CarClass;
import io.legacyfighter.cabs.crm.Client;
import io.legacyfighter.cabs.crm.claims.Claim;
import io.legacyfighter.cabs.driverfleet.DriverDTO;
import io.legacyfighter.cabs.geolocation.GeocodingService;
import io.legacyfighter.cabs.geolocation.address.Address;
import io.legacyfighter.cabs.geolocation.address.AddressDTO;
import io.legacyfighter.cabs.ride.Transit;
import io.legacyfighter.cabs.ride.TransitDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static java.util.stream.IntStream.range;


@Component
public class Fixtures {

    @Autowired
    AddressFixture addressFixture;

    @Autowired
    ClaimFixture claimFixture;

    @Autowired
    DriverFixture driverFixture;

    @Autowired
    ClientFixture clientFixture;

    @Autowired
    TransitFixture transitFixture;

    @Autowired
    AwardsAccountFixture awardsAccountFixture;

    @Autowired
    CarTypeFixture carTypeFixture;

    @Autowired
    RideFixture rideFixture;

    public Address anAddress() {
        return addressFixture.anAddress();
    }

    public AddressDTO anAddress(GeocodingService geocodingService, String country, String city, String street, int buildingNumber) {
        return addressFixture.anAddress(geocodingService, country, city, street, buildingNumber);
    }

    public Client aClient() {
        return clientFixture.aClient();
    }

    public Client aClient(Client.Type type) {
        return clientFixture.aClient(type);
    }

    public Transit transitDetails(DriverDTO driver, Integer price, LocalDateTime when, Client client) {
        return transitFixture.transitDetails(driver.id, price, when, client, anAddress(), anAddress());
    }

    public Transit transitDetails(DriverDTO driver, Integer price, LocalDateTime when) {
        return transitFixture.transitDetails(driver.id, price, when, aClient(), anAddress(), anAddress());
    }

    public TransitDTO aTransitDTO(AddressDTO from, AddressDTO to) {
        return transitFixture.aTransitDTO(aClient(), from, to);
    }

    public Transit aRide(int price, Client client, DriverDTO driver, Address from, Address destination) {
        return rideFixture.aRide(price, client, driver, from, destination);
    }

    public TransitDTO aRideWithFixedClock(int price, Instant publishedAt, Instant completedAt, Client client, DriverDTO driver, Address from, Address destination, Clock clock) {
        return rideFixture.aRideWithFixedClock(price, publishedAt, completedAt, client, driver, from, destination, clock);
    }

    public void anActiveCarCategory(CarClass carClass) {
        carTypeFixture.anActiveCarCategory(carClass);
    }

    public void clientHasDoneTransits(Client client, int noOfTransits, GeocodingService geocodingService) {
        range(1, noOfTransits + 1)
                .forEach(i -> {
                    Address pickup = anAddress();
                    DriverDTO driver = aNearbyDriver(geocodingService, pickup);
                    aRide(10, client, driver, pickup, anAddress());
                });
    }

    public Claim createClaim(Client client, Transit transit) {
        return claimFixture.createClaim(client, transit);
    }

    public Claim createClaim(Client client, TransitDTO transit, String reason) {
        return claimFixture.createClaim(client, transit, reason);
    }

    public Claim createAndResolveClaim(Client client, Transit transit) {
        return claimFixture.createAndResolveClaim(client, transit);
    }

    public void clientHasDoneClaimAfterCompletedTransit(Client client, int howMany) {
        IntStream
                .range(1, howMany + 1).forEach(i -> createAndResolveClaim(client, transitDetails(driverFixture.aDriver(), 20, LocalDateTime.now(), client)));
    }

    public Client aClientWithClaims(Client.Type type, int howManyClaims) {
        Client client = clientFixture.aClient(type);
        clientHasDoneClaimAfterCompletedTransit(client, howManyClaims);
        return client;
    }

    public void activeAwardsAccount(Client client) {
        awardsAccountFixture.activeAwardsAccount(client);
    }

    public DriverDTO aNearbyDriver(GeocodingService geocodingService, Address pickup) {
        return driverFixture.aDriver();
    }

    public DriverDTO aDriver() {
        return driverFixture.aDriver();
    }
}
