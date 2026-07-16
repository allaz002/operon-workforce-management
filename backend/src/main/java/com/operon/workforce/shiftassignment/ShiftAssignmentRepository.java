package com.operon.workforce.shiftassignment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftAssignmentRepository extends JpaRepository<ShiftAssignment, Long> {
    boolean existsByShift_IdAndUser_Id(Long shiftId, Long userId);
}
