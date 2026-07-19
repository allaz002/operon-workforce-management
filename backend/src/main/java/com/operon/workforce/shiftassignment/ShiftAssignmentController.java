package com.operon.workforce.shiftassignment;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public ResponseEntity<List<ShiftAssignmentResponse>> getShiftAssignments(@PathVariable Long shiftId) {
        List<ShiftAssignmentResponse> response = shiftAssignmentService.getShiftAssignments(shiftId);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{assignmentId}")
    public ResponseEntity<Void> deleteShiftAssignment(@PathVariable Long assignmentId, @PathVariable Long shiftId) {
        shiftAssignmentService.deleteShiftAssignment(assignmentId, shiftId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
