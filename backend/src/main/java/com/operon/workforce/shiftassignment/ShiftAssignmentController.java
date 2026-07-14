package com.operon.workforce.shiftassignment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shifts/{shiftId}/assignments")
public class ShiftAssignmentController {
    private final ShiftAssignmentService shiftAssignmentService;

    public ShiftAssignmentController(ShiftAssignmentService shiftAssignmentService) {
        this.shiftAssignmentService = shiftAssignmentService;
    }

    @PostMapping
    public ResponseEntity<ShiftAssignmentResponse> createShiftAssignment(@PathVariable Long shiftId, @Valid @RequestBody CreateShiftAssignmentRequest request) {
        ShiftAssignmentResponse response = shiftAssignmentService
                .createShiftAssignment(shiftId, request.userId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
