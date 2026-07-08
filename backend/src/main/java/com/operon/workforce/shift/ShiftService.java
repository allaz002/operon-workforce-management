package com.operon.workforce.shift;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShiftService {
    private final ShiftRepository shiftRepository;

    public ShiftService(ShiftRepository shiftRepository) {
        this.shiftRepository = shiftRepository;
    }

    @Transactional
    public ShiftResponse createShift(CreateShiftRequest createShiftRequest) {
        Shift shift = new Shift(
                createShiftRequest.startTime(),
                createShiftRequest.endTime(),
                createShiftRequest.role(),
                createShiftRequest.requiredEmployees(),
                createShiftRequest.location(),
                createShiftRequest.note()
        );
        Shift savedShift = shiftRepository.save(shift);
        return toResponse(savedShift);
    }

    private ShiftResponse toResponse(Shift shift) {
        return new ShiftResponse(
                shift.getId(),
                shift.getStartTime(),
                shift.getEndTime(),
                shift.getRole(),
                shift.getRequiredEmployees(),
                shift.getLocation(),
                shift.getNote(),
                shift.getCreatedAt()
        );
    }

}
