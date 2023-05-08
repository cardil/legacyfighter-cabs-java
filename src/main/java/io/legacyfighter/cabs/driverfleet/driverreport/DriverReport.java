package io.legacyfighter.cabs.driverfleet.driverreport;


import io.legacyfighter.cabs.driverfleet.DriverDTO;
import io.legacyfighter.cabs.ride.TransitDTO;
import io.legacyfighter.cabs.tracking.DriverSessionDTO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverReport {

    private DriverDTO driverDTO;

    private Map<DriverSessionDTO, List<TransitDTO>> sessions = new HashMap<>();

    public DriverDTO getDriverDTO() {
        return driverDTO;
    }

    public void setDriverDTO(DriverDTO driverDTO) {
        this.driverDTO = driverDTO;
    }

    public Map<DriverSessionDTO, List<TransitDTO>> getSessions() {
        return sessions;
    }

    public void setSessions(Map<DriverSessionDTO, List<TransitDTO>> sessions) {
        this.sessions = sessions;
    }
}

