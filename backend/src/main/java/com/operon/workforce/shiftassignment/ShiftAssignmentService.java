package com.operon.workforce.shiftassignment;

import com.operon.workforce.shift.Shift;
import com.operon.workforce.shift.ShiftNotFoundException;
import com.operon.workforce.shift.ShiftRepository;
import com.operon.workforce.user.User;
import com.operon.workforce.user.UserNotFoundException;
import com.operon.workforce.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShiftAssignmentService {

    private final ShiftAssignmentRepository shiftAssignmentRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    public ShiftAssignmentService(ShiftAssignmentRepository shiftAssignmentRepository, ShiftRepository shiftRepository, UserRepository userRepository) {
        this.shiftAssignmentRepository = shiftAssignmentRepository;
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ShiftAssignmentResponse createShiftAssignment(Long shiftId, Long userId) {
        Shift shift = shiftRepository.findById(shiftId).orElseThrow(ShiftNotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (shiftAssignmentRepository.existsByShift_IdAndUser_Id(shift.getId(), user.getId())) {
            throw new ShiftAssignmentAlreadyExistsException();
        }

        ShiftAssignment assignment = new ShiftAssignment(
                shift,
                user
        );
        shiftAssignmentRepository.save(assignment);

        return toResponse(assignment);
    }

    @Transactional
    public List<ShiftAssignmentResponse> getShiftAssignments(Long shiftId) {
        shiftRepository.findById(shiftId).orElseThrow(ShiftNotFoundException::new);
        List<ShiftAssignment> assignments = shiftAssignmentRepository.findByShift_Id(shiftId);
        List<ShiftAssignmentResponse> responses = new ArrayList<>();

        for (ShiftAssignment assignment : assignments) {
            responses.add(toResponse(assignment));
        }

        return responses;
    }

    private ShiftAssignmentResponse toResponse(ShiftAssignment shiftAssignment) {
        return new ShiftAssignmentResponse(
                shiftAssignment.getId(),
                shiftAssignment.getShiftId(),
                shiftAssignment.getUserId(),
                shiftAssignment.getCreatedAt()
        );
    }
}
