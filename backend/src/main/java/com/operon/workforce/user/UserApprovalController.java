package com.operon.workforce.user;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserApprovalController {
    private final UserApprovalService userApprovalService;

    public UserApprovalController(UserApprovalService userApprovalService) {
        this.userApprovalService = userApprovalService;
    }

    @PatchMapping("/{userId}/approve")
    public ResponseEntity<UserResponse> approveUser(@PathVariable("userId") Long userId) {
        UserResponse userResponse = userApprovalService.approveUser(userId);

        return ResponseEntity.ok(userResponse);
    }
}
