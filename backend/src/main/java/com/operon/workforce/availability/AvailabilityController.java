package com.operon.workforce.availability;

import com.operon.workforce.auth.AuthenticatedUser;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/availabilities")
public class AvailabilityController {
    private final AvailabilityService availabilityService;

    public AvailabilityController(AvailabilityService availabilityService) {
        this.availabilityService = availabilityService;
    }

    @PostMapping
    public ResponseEntity<AvailabilityResponse> createAvailability(Authentication authentication, @Valid @RequestBody CreateAvailabilityRequest request) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        AvailabilityResponse response = availabilityService.createAvailability(
                authenticatedUser.userId(),
                request
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<AvailabilityResponse>> getAvailability(Authentication authentication) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = authenticatedUser.userId();

        return ResponseEntity.ok(availabilityService.getAvailabilitiesForUser(userId));
    }

    @DeleteMapping("/{availabilityId}")
    public ResponseEntity<Void> deleteAvailability(Authentication authentication, @PathVariable Long availabilityId) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = authenticatedUser.userId();

        availabilityService.deleteAvailability(userId, availabilityId);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{availabilityId}")
    public ResponseEntity<AvailabilityResponse> updateAvailability(Authentication authentication, @PathVariable Long availabilityId, @Valid @RequestBody UpdateAvailabilityRequest request) {
        AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
        Long userId = authenticatedUser.userId();

        AvailabilityResponse response = availabilityService.updateAvailability(userId, availabilityId, request);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    public ResponseEntity<List<AvailabilityResponse>> getAllAvailabilities() {
        List<AvailabilityResponse> availabilityResponses = availabilityService.getAllAvailabilities();

        return ResponseEntity.ok(availabilityResponses);
    }

}
