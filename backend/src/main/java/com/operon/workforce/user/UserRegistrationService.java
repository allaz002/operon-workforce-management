package com.operon.workforce.user;

import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;

    public UserRegistrationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse registerUser(RegisterUserRequest request) {
        if (this.userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = new User(request.firstName(), request.lastName(), request.email(), request.password(),
                UserRole.EMPLOYEE);
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(),
                savedUser.getEmail(), savedUser.getRole(), savedUser.getApprovalStatus(), savedUser.getCreatedAt());
        
    }
}
