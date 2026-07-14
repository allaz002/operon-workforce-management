package com.operon.workforce.shiftassignment;

import com.operon.workforce.shift.Shift;
import com.operon.workforce.shift.ShiftNotFoundException;
import com.operon.workforce.shift.ShiftRepository;
import com.operon.workforce.user.User;
import com.operon.workforce.user.UserNotFoundException;
import com.operon.workforce.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

        ShiftAssignment assignment = new ShiftAssignment(
                shift,
                user
        );
        shiftAssignmentRepository.save(assignment);

        return toResponse(assignment);
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
