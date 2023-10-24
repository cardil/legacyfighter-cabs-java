package io.legacyfighter.cabs.ride.assigments;

import io.legacyfighter.cabs.assignment.DriverAssignmentFacade;
import io.legacyfighter.cabs.ride.RideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class AssignmentController {
    @Autowired
    private DriverAssignmentFacade facade;

    @Autowired
    private RideService rideService;

    @GetMapping("/transit-assignments/{driverId}")
    public TransitAssignments transitAssignmentsForDriver(@PathVariable Long driverId) {
        Set<UUID> assigments = facade.proposedAssignmentsForDriver(driverId);
        return new TransitAssignments(
            assigments.stream()
                .map(rideService::loadTransit)
                .collect(Collectors.toSet())
        );
    }
}
