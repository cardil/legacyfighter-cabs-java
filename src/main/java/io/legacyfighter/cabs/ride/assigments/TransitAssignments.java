package io.legacyfighter.cabs.ride.assigments;

import io.legacyfighter.cabs.ride.TransitDTO;

import java.util.Collections;
import java.util.Set;

public class TransitAssignments {
    final Set<TransitDTO> transits;

    public TransitAssignments(Set<TransitDTO> transits) {
        this.transits = Collections.unmodifiableSet(transits);
    }
}
