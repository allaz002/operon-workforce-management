package com.operon.workforce.shift;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

    @Transactional(readOnly = true)
    public List<ShiftResponse> getAllShifts() {
        List<Shift> shifts = shiftRepository.findAllByOrderByStartTimeAsc();
        List<ShiftResponse> shiftResponses = new ArrayList<>();

        for (Shift shift : shifts) {
            shiftResponses.add(toResponse(shift));
        }

        return shiftResponses;
    }

    @Transactional
    public ShiftResponse updateShift(UpdateShiftRequest updateShiftRequest, Long shiftId) {
        Shift shift = shiftRepository
                .findById(shiftId)
                .orElseThrow(ShiftNotFoundException::new);

        shift.update(
                updateShiftRequest.startTime(),
                updateShiftRequest.endTime(),
                updateShiftRequest.role(),
                updateShiftRequest.requiredEmployees(),
                updateShiftRequest.location(),
                updateShiftRequest.note()
        );

        return toResponse(shift);
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
