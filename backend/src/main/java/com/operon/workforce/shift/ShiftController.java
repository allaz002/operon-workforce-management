package com.operon.workforce.shift;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shifts")
public class ShiftController {
    private final ShiftService shiftService;

    public ShiftController(ShiftService shiftService) {
        this.shiftService = shiftService;
    }

    @PostMapping()
    public ResponseEntity<ShiftResponse> createShift(@Valid @RequestBody CreateShiftRequest createShiftRequest) {
        ShiftResponse shiftResponse = shiftService.createShift(createShiftRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(shiftResponse);
    }
}
