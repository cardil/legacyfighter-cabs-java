package io.legacyfighter.cabs.driverfleet.driverreport;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class DriverReportController {

    @GetMapping("/driverreport/{driverId}")
    @Transactional
    public DriverReport loadReportForDriver(@PathVariable Long driverId, @RequestParam int lastDays) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
