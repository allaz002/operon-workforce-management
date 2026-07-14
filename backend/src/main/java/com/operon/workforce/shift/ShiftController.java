package com.operon.workforce.shift;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {
    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping
    public ResponseEntity<ShiftResponse> createShift(@Valid @RequestBody CreateShiftRequest createShiftRequest) {
        ShiftResponse shiftResponse = shiftService.createShift(createShiftRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(shiftResponse);
    }

    @GetMapping
    public ResponseEntity<List<ShiftResponse>> getAllShifts() {
        List<ShiftResponse> shiftResponses = shiftService.getAllShifts();

        return ResponseEntity.status(HttpStatus.OK).body(shiftResponses);
    }

    @PutMapping("/{shiftId}")
    public ResponseEntity<ShiftResponse> updateShift(@PathVariable Long shiftId, @Valid @RequestBody UpdateShiftRequest updateShiftRequest) {
        ShiftResponse shiftResponse = shiftService.updateShift(updateShiftRequest, shiftId);

        return ResponseEntity.ok(shiftResponse);
    }

    @DeleteMapping("/{shiftId}")
    public ResponseEntity<Void> deleteShift(@PathVariable Long shiftId) {
        shiftService.deleteShift(shiftId);

        return ResponseEntity.noContent().build();

    }
}
