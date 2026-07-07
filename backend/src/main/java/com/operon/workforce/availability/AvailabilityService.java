package com.operon.workforce.availability;

import com.operon.workforce.user.User;
import com.operon.workforce.user.UserNotFoundException;
import com.operon.workforce.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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

        Availability availability = new Availability(
                user,
                startTime,
                endTime,
                createAvailabilityRequest.note()
        );
        Availability savedAvailability = availabilityRepository.save(availability);

        return toResponse(savedAvailability);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAvailabilitiesForUser(Long userId) {
        List<Availability> availabilities = availabilityRepository.findByUserIdOrderByStartTimeAsc(userId);
        List<AvailabilityResponse> availabilitiesResponse = new ArrayList<>();

        for (Availability availability : availabilities) {
            availabilitiesResponse.add(toResponse(availability));
        }

        return availabilitiesResponse;
    }

    @Transactional
    public void deleteAvailability(Long userId, Long availabilityId) {
        Availability availability = availabilityRepository
                .findByIdAndUser_Id(availabilityId, userId)
                .orElseThrow(AvailabilityNotFoundException::new);

        availabilityRepository.delete(availability);
    }

    @Transactional
    public AvailabilityResponse updateAvailability(Long userId, Long availabilityId, UpdateAvailabilityRequest updateAvailabilityRequest) {
        Availability availability = availabilityRepository
                .findByIdAndUser_Id(availabilityId, userId)
                .orElseThrow(AvailabilityNotFoundException::new);

        Instant startTime = updateAvailabilityRequest.startTime();
        Instant endTime = updateAvailabilityRequest.endTime();
        String note = updateAvailabilityRequest.note();

        availability.update(startTime, endTime, note);

        return toResponse(availability);
    }

    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAllAvailabilities() {
        List<Availability> availabilities = availabilityRepository.findAllByOrderByStartTimeAsc();
        List<AvailabilityResponse> availabilitiesResponses = new ArrayList<>();

        for (Availability availability : availabilities) {
            availabilitiesResponses.add(toResponse(availability));
        }

        return availabilitiesResponses;
    }

    private AvailabilityResponse toResponse(Availability availability) {
        return new AvailabilityResponse(
                availability.getId(),
                availability.getUser().getId(),
                availability.getStartTime(),
                availability.getEndTime(),
                availability.getNote(),
                availability.getCreatedAt()
        );
    }
}
