package io.legacyfighter.cabs.assignment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;
import java.util.UUID;

interface DriverAssignmentRepository extends JpaRepository<DriverAssignment, Long> {

    DriverAssignment findByRequestUUID(UUID transitRequestUUID);

    DriverAssignment findByRequestUUIDAndStatus(UUID transitRequestUUID, AssignmentStatus status);

    @Query("select a from DriverAssignment a where a.assignedDriver is null and a.status = 'WAITING_FOR_DRIVER_ASSIGNMENT'")
    Set<DriverAssignment> findUnassigned();
}
