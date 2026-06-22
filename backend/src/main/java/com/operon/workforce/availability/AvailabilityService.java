package com.operon.workforce.availability;

import com.operon.workforce.user.User;
import com.operon.workforce.user.UserNotFoundException;
import com.operon.workforce.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AvailabilityService {
    private final AvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;

    public AvailabilityService(AvailabilityRepository availabilityRepository, UserRepository userRepository) {
        this.availabilityRepository = availabilityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AvailabilityResponse createAvailability(Long userId, CreateAvailabilityRequest createAvailabilityRequest) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        Instant startTime = createAvailabilityRequest.startTime();
        Instant endTime = createAvailabilityRequest.endTime();

        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException();
        }

        Availability availability = new Availability(
                user,
                startTime,
                endTime,
                createAvailabilityRequest.note()
        );
        Availability savedAvailability = availabilityRepository.save(availability);

        return new AvailabilityResponse(
                savedAvailability.getId(),
                savedAvailability.getUser().getId(),
                savedAvailability.getStartTime(),
                savedAvailability.getEndTime(),
                savedAvailability.getNote(),
                savedAvailability.getCreatedAt()
        );
    }
}
