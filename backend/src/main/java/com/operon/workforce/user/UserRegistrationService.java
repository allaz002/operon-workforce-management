package com.operon.workforce.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserRegistrationService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse registerUser(RegisterUserRequest request) {
        if (this.userRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        String passwordHash = passwordEncoder.encode(request.password());

        User user = new User(request.firstName(), request.lastName(), request.email(), passwordHash,
                UserRole.EMPLOYEE);
        User savedUser = userRepository.save(user);

        return new UserResponse(savedUser.getId(), savedUser.getFirstName(), savedUser.getLastName(),
                savedUser.getEmail(), savedUser.getRole(), savedUser.getApprovalStatus(), savedUser.getCreatedAt());

    }
}
