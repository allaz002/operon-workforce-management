package com.operon.workforce.availability;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {
    List<Availability> findByUserIdOrderByStartTimeAsc(Long userId);
}
