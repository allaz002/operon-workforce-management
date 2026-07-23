package com.operon.workforce.shiftassignment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {
    boolean existsByShift_IdAndUser_Id(Long shiftId, Long userId);

    List<ShiftAssignment> findByShift_Id(Long shiftId);

    Optional<ShiftAssignment> findByIdAndShift_Id(Long assignmentId, Long shiftId);

    long countByShift_Id(Long shiftId);

    boolean existsByUser_IdAndShift_StartTimeLessThanAndShift_EndTimeGreaterThan(
            Long userId,
            Instant startTime,
            Instant endTime
    );
}
