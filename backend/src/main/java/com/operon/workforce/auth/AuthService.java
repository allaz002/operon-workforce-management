package com.operon.workforce.auth;

import com.operon.workforce.user.User;
import com.operon.workforce.user.UserApprovalStatus;
import com.operon.workforce.user.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public LoginResponse login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(InvalidLoginException::new);

        if (!passwordEncoder.matches(loginRequest.password(), user.getPasswordHash())) {
            throw new InvalidLoginException();
        }

        if (user.getApprovalStatus() != UserApprovalStatus.APPROVED) {
            throw new UserNotApprovedException();
        }

        return new LoginResponse(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getRole(),
                user.getApprovalStatus());
    }
}
