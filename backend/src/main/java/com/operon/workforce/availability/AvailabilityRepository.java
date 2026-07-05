package com.operon.workforce.availability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByUserIdOrderByStartTimeAsc(Long userId);

    Optional<Availability> findByIdAndUser_Id(Long id, Long userId);
}
